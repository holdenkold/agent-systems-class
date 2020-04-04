package com.company;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.core.AID;


public class Client extends Agent {
    protected void setup() {
        Behaviour bookTable = new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
                msg.addReceiver(new AID("gate_keeper_agent", AID.ISLOCALNAME));
                String content = "Client: Can we book a table for two for tomorrow 7 o'clock?";
                msg.setContent(content);
                System.out.println(content);
                send(msg);
            }
        };

        Behaviour getAnswer = new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    switch (rcv.getPerformative()) {
                        case ACLMessage.CONFIRM:
                            System.out.println("Client: Great! See you!");
                            ACLMessage response = rcv.createReply();
                            response.setPerformative(ACLMessage.INFORM);
                            response.setContent("Confirming booking");
                            send(response);
                            break;
                        case ACLMessage.DISCONFIRM:
                            System.out.println("Client: Sorry to hear this :(");
                            System.exit(0);
                    }
                } else {
                    block();
                }
            }
        };
        addBehaviour(bookTable);
        addBehaviour(getAnswer);
    }
    protected void takeDown() {
        super.takeDown();
    }
}