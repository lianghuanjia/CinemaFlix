package edu.uci.ics.huanjial.service.api_gateway.threadpool;

import edu.uci.ics.huanjial.service.api_gateway.logger.ServiceLogger;

import java.security.SecureRandom;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue() {
        head = null;
        tail = null;
    }

    public synchronized void enqueue(ClientRequest clientRequest) {
        if(head == null){
            ListNode node = new ListNode(clientRequest, null); // create a new node. The "next" of the node is the tail.
            head = node; //head points to node
            tail = node;
            this.notify();
        }
        else{
            //after the while loop, the temp's next is null, and the temp is pointing to the tail.
            ListNode node = new ListNode(clientRequest, null);
            tail.setNext(node);
            tail = node;
        }
    }

    public synchronized ClientRequest dequeue() {
        while (head == null) {
            try {
                this.wait();
            }
            catch (InterruptedException e){
                ServiceLogger.LOGGER.info("ClientRequest dequeue exception");
            }
        }
        ListNode temp = head;
        head = head.getNext();
        return temp.getClientRequest();
    }

    boolean isEmpty() {
        return (head == null);
    }
}
