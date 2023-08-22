package edu.uci.ics.huanjial.service.api_gateway.threadpool;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;

public class ThreadPool {
    private int numWorkers;
    private Worker[] workers;
    private ClientRequestQueue queue;

    public ThreadPool(int numWorkers) {
        queue = new ClientRequestQueue();
        ServiceLogger.LOGGER.info("ThreadPool constructor.");
        workers = new Worker[numWorkers];
        for(int i = 0; i < numWorkers; i++){
            Worker worker = Worker.CreateWorker(i, this);
            workers[i] = worker;
        }
        ServiceLogger.LOGGER.info("Finish creating a ThreadPool.");
    }

    //add the clientRequest into the ClientRequestQueue?
    public void add(ClientRequest clientRequest) {
        queue.enqueue(clientRequest);
    }

    public ClientRequest remove(){
        return queue.dequeue();
    }

    public ClientRequestQueue getQueue() {
        return queue;
    }
}
