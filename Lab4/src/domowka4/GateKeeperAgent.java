package domowka4;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class GateKeeperAgent extends Agent {
	
	private String cuisine, restaurantName;

	@Override
	protected void setup() {
		super.setup();
		readArguments();
		Behaviour registrationBehaviour = createRegistrationBehaviour();
		Behaviour reservationBehaviour = createReservationBehaviour();
		addBehaviour(registrationBehaviour);
		addBehaviour(reservationBehaviour);
	}

	private Behaviour createReservationBehaviour() {
		return new SimpleBehaviour() {

			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					System.out.println(getLocalName() + ": " + "I have been asked for reservation!");
					ACLMessage resp = msg.createReply();
					Random r = new Random();
					int isAvailable = r.nextInt(2);
					if (isAvailable == 1) {
						resp.setContent("available");
					} else {
						resp.setContent("available");
					}
					send(resp);
				} else {
					block();
				}
			}

			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}

	private Behaviour createRegistrationBehaviour() {
		return new OneShotBehaviour() {

			@Override
			public void action() {
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType(cuisine);
				sd.setName(restaurantName);
				dfd.addServices(sd);
				try {
					DFService.register(myAgent, dfd); 
					}
				catch (FIPAException fe) 
				{ 
					fe.printStackTrace();
				}  
			}
		};
	}

	private void readArguments() {
		Object[] args = getArguments();
		cuisine = (String)args[0];
		restaurantName = (String)args[1];
	}

	@Override
	protected void takeDown() {
		try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println(getAID().getName()+" closing.");
        super.takeDown();
	}

}
