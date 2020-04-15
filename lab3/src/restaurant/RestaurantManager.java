package restaurant;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class RestaurantManager extends Agent {
    Random random = new Random();
    private void generateAnswer(ACLMessage response)
    {
        if(random.nextBoolean())
            response.setPerformative(ACLMessage.CONFIRM);
        else
            response.setPerformative(ACLMessage.DISCONFIRM);
    }

    protected void setup() {
        Behaviour checkAvailability = new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.QUERY_IF:
                            ACLMessage response = rcv.createReply();
                            generateAnswer(response);
                            System.out.println("Manager checking ...");
                            send(response);
                            break;
                    }
                }
                else {
                    block();
                }
            }
        };
        addBehaviour(checkAvailability);
    }
    protected void takeDown() {
        super.takeDown();
    }
}