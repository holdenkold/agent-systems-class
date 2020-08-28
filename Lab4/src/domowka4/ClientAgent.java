package domowka4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

	@Override
	protected void setup() {
		super.setup();
		Behaviour reservationBehaviour = createReservationBehaviour();
		addBehaviour(reservationBehaviour);
	}

	private Behaviour createReservationBehaviour() {
		Behaviour reservationBehaviour = new OneShotBehaviour() {
			private String cuisine;
			private int time;
			AID[] tempAgents;
			
            @Override
            public void action() {
                readUserConfiguration();
                findRestaurant();
                askRestaurant();
                confirmReservaition();
                
            }

			private void confirmReservaition() {
				List<ACLMessage> responses = new ArrayList<>();
				if(tempAgents.length == 0)
				{	System.out.println("No restaurants available");
					return;
				}
				for(int i = 0; i < tempAgents.length; i++)
					responses.add(blockingReceive());
				System.out.println("Available restaurants");
			}

			private void askRestaurant() {
				ACLMessage ask = new ACLMessage(ACLMessage.QUERY_IF);
				ask.setContent( "Gatekeeper: Do we have empty table?");
				for(AID r : tempAgents)
				{
					ask.addReceiver(r);
                    System.out.println("Send ask for restaurant: " + r.getLocalName());
                    send(ask);
				}
			}

			private void findRestaurant() {
				DFAgentDescription template = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType(cuisine);
				template.addServices(sd);
				try {
					DFAgentDescription[ ] result = DFService.search(myAgent, template);
					tempAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						tempAgents[i] = result[i].getName();
					} 
				}
				catch (FIPAException fe) {
					fe.printStackTrace(); 
					}
			}

			private void readUserConfiguration() {
				Scanner sc = new Scanner(System.in);
				System.out.println("What you would like to eat?");
				cuisine = sc.nextLine();
				System.out.println("What time?");
				time = Integer.parseInt(sc.nextLine());
			}
        };
        return reservationBehaviour;
	}
	

	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}
}
