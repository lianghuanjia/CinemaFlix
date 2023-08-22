package edu.uci.ics.huanjial.service.api_gateway.threadpool;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.huanjial.service.api_gateway.utilities.General;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.lang.invoke.SerializedLambda;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;


    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
        this.start();
    }

    //Is this correct?
    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        Worker worker = new Worker(id, threadPool);
        return worker;
    }

    public void process() {
        ClientRequest clientRequest = threadPool.remove();

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Getting URI...");
        String URI = clientRequest.getURI();

        ServiceLogger.LOGGER.info("Getting endpoint...");
        String endpointPath = clientRequest.getEndpoint();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(URI).path(endpointPath);

        MultivaluedMap<String, String> queryParams = clientRequest.getQueryParams();
        if(queryParams != null){
            ServiceLogger.LOGGER.info("Request has queryParams");
            for(String key: queryParams.keySet()){
                webTarget = webTarget.queryParam(key, queryParams.getFirst(key));
            }
        }
        ServiceLogger.LOGGER.info("Finish building webtarget with query param. ");
        ServiceLogger.LOGGER.info("Webtarget is: " + webTarget.toString());
        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        System.out.println("In worker: ");
        invocationBuilder.header("email", clientRequest.getEmail());
        System.out.println("email" + clientRequest.getEmail());
        invocationBuilder.header("sessionID", clientRequest.getSessionID());
        System.out.println("sessionID" + clientRequest.getSessionID());
        invocationBuilder.header("transactionID", clientRequest.getTransactionID());
        System.out.println("transactionID" + clientRequest.getTransactionID());

        Response response = null;
        // Send the request and save it to a Response according to the request type:
        if(clientRequest.getRequestType().equals("POST")) {
            ServiceLogger.LOGGER.info("Sending POST request...");
            response = invocationBuilder.post(Entity.entity(clientRequest.getRequest(), MediaType.APPLICATION_JSON));
            ServiceLogger.LOGGER.info("Sent!");
        }
        else if(clientRequest.getRequestType().equals("GET")){
            ServiceLogger.LOGGER.info("Sending GET request...");
            response = invocationBuilder.get();
            ServiceLogger.LOGGER.info("Sent!");
        }
        else if(clientRequest.getRequestType().equals("DELETE")){
            ServiceLogger.LOGGER.info("Sending DELETE request...");
            response = invocationBuilder.delete();
            ServiceLogger.LOGGER.info("Sent!");
        }

        ServiceLogger.LOGGER.info("Finish getting response from micro service");
        ServiceLogger.LOGGER.info("Get data to put into database");

        String transactionID = clientRequest.getTransactionID();
        String email = clientRequest.getEmail();
        String sessionID = clientRequest.getSessionID();
        String text = response.readEntity(String.class); //readEntity reads jsonFormat and change it into String.
        Integer httpStatus = response.getStatus();

        //Put them into database.
        General.putDataIntoDatabase(transactionID, email, sessionID, text, httpStatus);
        //When you make a preparedStatement, we call getCon form the connection pool

        //After we finish it, we need to release the connection.


         /*
        ClientRequest clientRequest = threadPool.remove();
        if (clientRequest == null) {
            return;
        }
        ServiceLogger.LOGGER.info("Processing request # " + clientRequest.getEmail() + " at " + (System.currentTimeMillis() / 1000) % 60 + " seconds.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

          */

    }

    @Override
    public void run() {
        while (true) {
            process();
        }
    }
}
