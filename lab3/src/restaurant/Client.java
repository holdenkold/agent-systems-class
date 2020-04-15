package restaurant;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.DFService;


public class Client extends Agent {
    private AID [] serving_restaurants;
    private String content = "Client: Can we book a table for two for tomorrow 7 o'clock?";
    protected void setup() {
        Behaviour searchReastaurants = new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Polish restaurant");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    serving_restaurants = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        serving_restaurants[i] = result[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        };

        OneShotBehaviour callForProposal = new OneShotBehaviour() {
            @Override
            public void action() {
                if(serving_restaurants.length == 0)
                {
                    System.out.println("No restaurants serving Polish today :(");
                    System.exit(0);
                }
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < serving_restaurants.length; ++i) {
                    System.out.println("Agent: Asking for table");
                    cfp.addReceiver(serving_restaurants[i]);
                }
                cfp.setContent(content);
                send(cfp);
            }
        };

        addBehaviour(searchReastaurants);
        addBehaviour(callForProposal);

    }
        protected void takeDown() {
            super.takeDown();
        }
}
