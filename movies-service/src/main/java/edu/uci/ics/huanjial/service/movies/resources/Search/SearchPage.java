package edu.uci.ics.huanjial.service.movies.resources.Search;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.huanjial.service.movies.General.GeneralHelper;
import edu.uci.ics.huanjial.service.movies.Models.Search.MovieModel;
import edu.uci.ics.huanjial.service.movies.Models.Search.SearchResponseModel;
import edu.uci.ics.huanjial.service.movies.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers,
                           @QueryParam("title") String title,
                           @QueryParam("genre") String genre,
                           @QueryParam("year") Integer year,
                           @QueryParam("director") String director,
                           @QueryParam("hidden") Boolean hidden,
                           @QueryParam("limit") Integer limit,
                           @QueryParam("offset") Integer offset,
                           @QueryParam("orderby") String orderby,
                           @QueryParam("direction") String direction
                           ){

        ServiceLogger.LOGGER.info("Search");
        SearchResponseModel responseModel;

          // variable x = (condition)? (true val):(false val)
        limit = (limit == null || (limit != 10 && limit != 25 && limit != 50 && limit != 100))? 10:limit; //Need to check null first, then check < 0
        offset = (offset == null || offset < 0 || NotMultipleOfLimit(offset, limit))? 0:offset;
        orderby = (orderby == null|| (!orderby.equals("title")) && (!orderby.equals("rating")))? "rating":orderby; //title / rating
        direction = (direction == null || (!direction.equals("asc") && !direction.equals("desc")))? "desc":direction;

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");

        //Convert node object to String for later mapping

        printRequestLogger(headers,title, genre, year, director, hidden, offset, limit, orderby, direction);

        try{
            Boolean privilege = GeneralHelper.checkPrivilege(email, 3);

            SearchHelper sh = new SearchHelper();
            ArrayList<MovieModel> mmList = sh.searchMovie(title, genre, year, director, hidden, offset, limit, orderby, direction, privilege);

            if(mmList.isEmpty()){
                responseModel = new SearchResponseModel(211);
                responseModel.setMovies(null);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }
            else{
                responseModel = new SearchResponseModel(210);
                //Before we setMovies, we need to set the ArrayList<GenreModel>, ArrayList<StarModel>, & ArrayList<MovieModel> to array.
                MovieModel[] mmArray = mmList.toArray(new MovieModel[mmList.size()]);
                responseModel.setMovies(mmArray);
                return Response.status(Response.Status.OK).entity(responseModel).header("transactionID", transactionID).header("sessionID", sessionID).build();
            }

        }catch( SQLException e){ //We don't need to catch IOException here because we are not useing ObjectMapper.
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("transactionID", transactionID).header("sessionID", sessionID).build();
        }
    }

    private Boolean NotMultipleOfLimit(Integer offset, Integer limit){
        if(offset % limit != 0){
            return true;
        }
        return false;
    }

    private void printRequestLogger(HttpHeaders headers, String title, String genre, Integer year, String director,
                                    Boolean hidden, Integer offset, Integer limit, String sortby, String orderby){
        ServiceLogger.LOGGER.info("Email: " + headers.getHeaderString("email"));
        ServiceLogger.LOGGER.info("SessionID: " + headers.getHeaderString("sessionID"));
        ServiceLogger.LOGGER.info("Title: " + title);
        ServiceLogger.LOGGER.info("Genre: " + genre);
        ServiceLogger.LOGGER.info("Year: " + year);
        ServiceLogger.LOGGER.info("Director: " + director);
        ServiceLogger.LOGGER.info("Hidden: " + hidden);
        ServiceLogger.LOGGER.info("Offset: " + offset);
        ServiceLogger.LOGGER.info("Limit: " + limit);
        ServiceLogger.LOGGER.info("Sort by: " + sortby);
        ServiceLogger.LOGGER.info("Order by: " + orderby);
    }
}

        /*
        Only client with 1,2,3 level can see hidden. Not even 4. Before it was 4.
        LIMIT: valid value: 10(default), 25, 50, 100
        OFFSET: 0(default). If not, offset must be positive integer multiple of limit.
        ORDERBY: "title" / "rating". DEFAULT: "rating".
        DEFAULT: "asc" / "desc" (case doesn't matter). DEFAULT: "desc".
        primary sort: "rating" + "desc", secondary sort: "title" + "asc"
         */
