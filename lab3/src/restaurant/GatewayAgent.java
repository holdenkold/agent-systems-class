package restaurant;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

public class GatewayAgent extends Agent {
    private String service_type = "";
    public String service_name = "restaurant";
    protected void setup() {

        // randomly generation restaurant type
        int pick = new Random().nextInt(RestaurantTypes.values().length);
        this.service_type =  RestaurantTypes.values()[pick].toString() + " restaurant";

        // DFAgentDescription
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(service_type);
        sd.setName(service_name);
        dfd.addServices(sd);
        try {
            System.out.println(service_type + " "+ getAID().getName()+ " registering.");
            DFService.register( this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        Behaviour recieveRequest = new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    switch (rcv.getPerformative()) {
                        case ACLMessage.CFP:
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
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        //myGui.dispose();
        System.out.println(service_type+getAID().getName()+" closing.");
        super.takeDown();
    }
}
