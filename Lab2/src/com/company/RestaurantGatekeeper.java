package com.company;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

import java.util.Random;

public class RestaurantGatekeeper extends Agent {
    protected void setup() {
        Behaviour recieveRequest = new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    switch (rcv.getPerformative()) {
                        case ACLMessage.QUERY_IF:
                            ACLMessage ask = new ACLMessage(ACLMessage.QUERY_IF);
                            ask.addReceiver(new AID("manager_agent", AID.ISLOCALNAME));
                            ask.setContent( "Gatekeeper: Do we have empty table for tomorrow 7 o'clock?");
                            System.out.println("Gatekeeper: I will ask manager, wait a second.");
                            send(ask);
                            break;
                        case ACLMessage.DISCONFIRM:
                            ACLMessage disconfirm = new ACLMessage(ACLMessage.DISCONFIRM);
                            disconfirm.addReceiver(new AID("client_agent", AID.ISLOCALNAME));
                            System.out.println("Gatekeeper: Sorry, all table are booked :(");
                            send(disconfirm);
                            break;
                        case ACLMessage.CONFIRM:
                            ACLMessage confirm = new ACLMessage(ACLMessage.CONFIRM);
                            confirm.addReceiver(new AID("client_agent", AID.ISLOCALNAME));
                            System.out.println("Gatekeeper: We have a table for you!");
                            send(confirm);
                            break;
                        case ACLMessage.INFORM:
                            System.out.println("Gatekeeper: See you tomorrow!");
                            System.exit(0);
                    }
                }
                else {
                    block();
                }
            }
        };
        addBehaviour(recieveRequest);
    }
    protected void takeDown(){
        super.takeDown();
    }
}