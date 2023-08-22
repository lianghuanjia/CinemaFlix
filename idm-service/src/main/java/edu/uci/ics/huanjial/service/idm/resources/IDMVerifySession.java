package edu.uci.ics.huanjial.service.idm.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.idm.IDMService;
import edu.uci.ics.huanjial.service.idm.configs.Configs;
import edu.uci.ics.huanjial.service.idm.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.idm.models.IDMLoginResponseModel;
import edu.uci.ics.huanjial.service.idm.models.IDMVerifySessionRequestModel;
import edu.uci.ics.huanjial.service.idm.models.IDMVerifySessionResponseModel;
import edu.uci.ics.huanjial.service.idm.security.Session;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;


@Path("session")
public class IDMVerifySession {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public Response session(String jsonText){
        ServiceLogger.LOGGER.info("IDM Verify Session.");
        ServiceLogger.LOGGER.info("JSON: ");
        ServiceLogger.LOGGER.info(jsonText);
        IDMVerifySessionRequestModel sessionRequestModel = null;
        IDMVerifySessionResponseModel sessionResponseModel;
        ObjectMapper mapper = new ObjectMapper();
        ServiceLogger.LOGGER.info("Created a new Mapper");
        try{
            sessionRequestModel = mapper.readValue(jsonText, IDMVerifySessionRequestModel.class);
            ServiceLogger.LOGGER.info("Finish getting request model thro mapping");
            SessionHelper sessionHelper = new SessionHelper(sessionRequestModel.getEmail(), sessionRequestModel.getSessionID());
            if(sessionHelper.invalidTokenLen()){
                sessionResponseModel = new IDMVerifySessionResponseModel(-13);
                return Response.status(Response.Status.BAD_REQUEST).entity(sessionResponseModel).build();
            }
            else if(sessionHelper.invalidEmailFormat()){
                sessionResponseModel = new IDMVerifySessionResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).entity(sessionResponseModel).build();
            }
            else if(sessionHelper.invalidEmailLen()){
                sessionResponseModel = new IDMVerifySessionResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).entity(sessionResponseModel).build();
            }
            else if(sessionHelper.userNotFound()){ // userNotFound must be checked before session not found
                sessionResponseModel = new IDMVerifySessionResponseModel(14);
                return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
            }
            else{
                ServiceLogger.LOGGER.info("Look up session");
                int status = sessionHelper.lookUpSession();
                if(status == 0){ //Session Not Found
                    ServiceLogger.LOGGER.info("Session Not Found");
                    sessionResponseModel = new IDMVerifySessionResponseModel(134);
                    return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                }
                else if(status == 3){
                    ServiceLogger.LOGGER.info("Session status:  3");
                    sessionResponseModel = new IDMVerifySessionResponseModel(131);
                    return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                }
                else if(status == 2){
                    ServiceLogger.LOGGER.info("Session status: 2");
                    sessionResponseModel = new IDMVerifySessionResponseModel(132);
                    return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                }
                else if(status == 4){
                    ServiceLogger.LOGGER.info("Session status 4");
                    sessionResponseModel = new IDMVerifySessionResponseModel(133);
                    return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                }
                else{ // active.
                    ServiceLogger.LOGGER.info("Get a session");
                    Session session = sessionHelper.getSession();
                    //case 1: currentTime is within the valid Time Zone. We just call update to update the lastUsedTime
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis()); //Get the current time.
                    Configs configs = IDMService.getConfigs();
                    Timestamp timeoutTime = new Timestamp(session.getLastUsed().getTime() + configs.getTimeout());//Get the timeout time from CONFIGS!!
                    if(currentTime.before(timeoutTime)){
                        ServiceLogger.LOGGER.info("+ + + + + before timeout");
                        Timestamp nextTimeoutTime = new Timestamp(currentTime.getTime() + configs.getTimeout());
                        //The current time is valid, AND after 10 minutes it's still valid and not after expired time.
                        if(nextTimeoutTime.before(session.getExprTime())){
                            ServiceLogger.LOGGER.info("+ + + + + exp time");
                            ServiceLogger.LOGGER.info("+ + + + + renew Last Used Time.");
                            //session.update(); //update the lastUsed timestamp. The status is still ACTIVE. No need to change status.
                            //The above session.update will be called inside the SessionHelper.updateLastUsedTime.
                            //!!!!!!!
                            sessionHelper.updateLastUsedTime(session);
                            //!!!!!!!
                            sessionResponseModel = new IDMVerifySessionResponseModel(130);
                            sessionResponseModel.setSessionID(session.getSessionID().toString());//add the sessionID.
                            return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                        }
                        /*
                        else if(nextTimeoutTime.equals(session.getExprTime())){
                            session.update(); //should it count still active
                            sessionResponseModel = new IDMVerifySessionResponseModel(130);
                            sessionResponseModel.setSessionID(session.getSessionID().toString());
                            return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                        }

                         */
                        else{ //The current time is valid, BUT it's about to expire in LESS THAN 10 minutes
                            //we need to revoke the current session, and generate a new session.
                            ServiceLogger.LOGGER.info("Current time is valid, but it's about to expire soon");
                            sessionHelper.setStatusRevoked(session.getSessionID().toString()); //set the session as revoked.
                            String newSessionID = sessionHelper.createNewSession(session.getEmail());//make a new session
                            sessionResponseModel = new IDMVerifySessionResponseModel(130);
                            sessionResponseModel.setSessionID(newSessionID);
                            return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                        }
                    }
                    else if(currentTime.after(timeoutTime) && currentTime.before(session.getExprTime())){
                        //currentTime equals or after the timeoutTime AND before the expired time -> revoke
                        //set the session as revoked first.
                        ServiceLogger.LOGGER.info("Current time equals or after the timeoutTime and before the expired time");
                        sessionHelper.setStatusRevoked(session.getSessionID().toString());
                        sessionResponseModel = new IDMVerifySessionResponseModel(133); //return a revoked response
                        return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                    }
                    else{ //after the expired time.
                        //set the session's status as expired first.
                        ServiceLogger.LOGGER.info("After expired time");
                        sessionHelper.setStatusExpired(session.getSessionID().toString());
                        sessionResponseModel = new IDMVerifySessionResponseModel(131); //return a expired response
                        return Response.status(Response.Status.OK).entity(sessionResponseModel).build();
                    }
                }
            }


        }catch(IOException | SQLException e){
            ServiceLogger.LOGGER.info("Exception occurred.");
            if(e instanceof JsonParseException){
                ServiceLogger.LOGGER.info("JsonParseException");
                sessionResponseModel = new IDMVerifySessionResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).entity(sessionResponseModel).build();
            }
            else if(e instanceof JsonMappingException){
                ServiceLogger.LOGGER.info("JsonMappingException");
                sessionResponseModel = new IDMVerifySessionResponseModel(-2);
                ServiceLogger.LOGGER.info("Error message: ");
                ServiceLogger.LOGGER.info(((JsonMappingException) e).toString());
                return Response.status(Response.Status.BAD_REQUEST).entity(sessionResponseModel).build();
            }
            else{ // case -1
                ServiceLogger.LOGGER.info("Error message: " + ((Exception) e).toString());
                ServiceLogger.LOGGER.warning("Internal_server_error!!!!!!!!!!!!!!!!!!!!!");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
