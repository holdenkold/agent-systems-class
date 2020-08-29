package domowka4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ClientAgent extends Agent {
	
	@Override
	protected void afterMove() {
		if (restaurantHasBeenAsked && !allRestaurantsAsked)
			sendAskForRestaurant();
	}
		
	private void sendAskForRestaurant() {
		ACLMessage ask = new ACLMessage(ACLMessage.QUERY_IF);
		ask.setContent( "Gatekeeper: Do we have empty table?");
        ask.addReceiver(restaurantBeingAsked);
        System.out.println("Send ask for restaurant: " + restaurantBeingAsked.getLocalName());
        send(ask);
		restaurantHasBeenAsked = false;
	}

	private AID restaurantBeingAsked = null;
	private boolean allRestaurantsAsked = false;
	private boolean userConfigurationRead = false;
	private boolean restaurantFound = false;
	private boolean restaurantHasBeenAsked = false;
	private boolean userInformed = false;
	private boolean reservationConfirmed = false;
	private List<ACLMessage> responses = new ArrayList<>();
	private int choosedRestaurantNr = 0;
	private Stack<AID> tempAgentsStack = new Stack<AID>();
	private AID[] tempAgents;
	private SLCodec codec = new SLCodec();
	private Ontology mobilityOntology = MobilityOntology.getInstance();
	
	@Override
	protected void setup() {
		super.setup();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(mobilityOntology);
		Behaviour reservationBehaviour = createReservationBehaviour();
		addBehaviour(reservationBehaviour);
	}

	private Behaviour createReservationBehaviour() {
		Behaviour reservationBehaviour = new SimpleBehaviour() {
			private String cuisine;
			private int time;
			private boolean done = false;
			
            @Override
            public void action() {
            	if(!userConfigurationRead) {
            		readUserConfiguration();
            	} else if(!restaurantFound) {
            		findRestaurant();
            	} else if(!restaurantHasBeenAsked) {
            		askRestaurant();
            	} else if(!userInformed && allRestaurantsAsked) {
            		informUser();
            	} else if(!reservationConfirmed && allRestaurantsAsked) {
            		confirmReservaition();
            	} else {
            		block();
            	}
            }

			private void confirmReservaition() {
				Location location = null;
				try 
				{
					ACLMessage locationRequest = new ACLMessage(ACLMessage.REQUEST);
					getContentManager().registerLanguage(codec);
					getContentManager().registerOntology(mobilityOntology);
					locationRequest.addReceiver(getAMS());
					locationRequest.setOntology(mobilityOntology.getName());
					locationRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					locationRequest.setLanguage(codec.getName());
					WhereIsAgentAction whereIsAgentAction = new WhereIsAgentAction();
					whereIsAgentAction.setAgentIdentifier(responses.get(choosedRestaurantNr).getSender());
					Action locationAction = new Action();
					locationAction.setActor(getAMS());
					locationAction.setAction(whereIsAgentAction);
					getContentManager().fillContent(locationRequest, locationAction);
					send(locationRequest);
					Result locationContent = (Result)getContentManager().extractContent(blockingReceive(MessageTemplate.MatchSender(getAMS())));
					location = (Location)locationContent.getItems().get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				doMove(location);
				
				ACLMessage confirmation = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				confirmation.setContent("Confirming");
				confirmation.addReceiver(responses.get(choosedRestaurantNr).getSender());
                System.out.println("Send confirmation to: " + responses.get(choosedRestaurantNr).getSender().getLocalName());
                send(confirmation);
				reservationConfirmed = true;
				done = true;
			}

			private void informUser() {
				if(tempAgents.length == 0)
				{	System.out.println("No restaurants available");
					userInformed = true;
					reservationConfirmed = true;
					return;
				}
				ACLMessage msg;
				for(int i = 0; i < tempAgents.length; i++) {
					 msg = receive();
					if (msg != null) {
						responses.add(msg);
					} else {
						block();
					}
				}
				if(responses.size() == tempAgents.length)
				{
					System.out.println("Available restaurants");
					int restaurant_nr = 1;
					for (ACLMessage resp : responses) {
						if (resp.getContent().equals("available")) {
							System.out.println(restaurant_nr + ": " + resp.getSender().getLocalName());
						}
						restaurant_nr++;
					}
					
					Scanner sc = new Scanner(System.in);
					System.out.println("What restaurant you choosing?");
					userInformed = true;
					choosedRestaurantNr =  Integer.parseInt(sc.nextLine()) - 1;
				}
			}

			private void askRestaurant() {
				Location location = null;
				try
				{
					ACLMessage locationRequest = new ACLMessage(ACLMessage.REQUEST);
					getContentManager().registerLanguage(codec);
					getContentManager().registerOntology(mobilityOntology);
					locationRequest.addReceiver(getAMS());
					locationRequest.setOntology(mobilityOntology.getName());
					locationRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					locationRequest.setLanguage(codec.getName());
					WhereIsAgentAction whereIsAgentAction = new WhereIsAgentAction();
					AID agentAID;
					if(tempAgentsStack.isEmpty()) {
						agentAID = getAMS();
						allRestaurantsAsked = true;
					} else {
						agentAID = tempAgentsStack.pop();
						restaurantBeingAsked = agentAID;
					}
					whereIsAgentAction.setAgentIdentifier(agentAID);
					Action locationAction = new Action();
					locationAction.setActor(getAMS());
					locationAction.setAction(whereIsAgentAction);
					getContentManager().fillContent(locationRequest, locationAction);
					send(locationRequest);
					Result locationContent = (Result)getContentManager().extractContent(blockingReceive(MessageTemplate.MatchSender(getAMS())));
					location = (Location)locationContent.getItems().get(0);
					} catch (Exception e) {
					e.printStackTrace();
				}
				doMove(location);
				restaurantHasBeenAsked = true;
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
						tempAgentsStack.push(result[i].getName());
					} 
				}
				catch (FIPAException fe) {
					fe.printStackTrace(); 
					}
				restaurantFound = true;
			}

			private void readUserConfiguration() {
				Scanner sc = new Scanner(System.in);
				System.out.println("What you would like to eat?");
				cuisine = sc.nextLine();
				System.out.println("What time?");
				time = Integer.parseInt(sc.nextLine());
				userConfigurationRead = true;
			}

			@Override
			public boolean done() {
				return done;
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
