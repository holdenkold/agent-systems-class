package domowka4;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ManagerAgent extends Agent{
	
	private static RestaurantInfo[] infos = { new RestaurantInfo("polska", "mloda_polka"),
			new RestaurantInfo("polska", "zajazd_pod_krowa"),
			new RestaurantInfo("polska", "mleczarnia_jerozolimska"),
			new RestaurantInfo("amerykanska", "mcdonalds"),
			new RestaurantInfo("amerykanska", "kfc"),
			new RestaurantInfo("amerykanska", "burger_king"),
			new RestaurantInfo("hiszpanska", "vayamos_companeros"),
			new RestaurantInfo("hiszpanska", "chica_loca")};

	@Override
	protected void setup() {
		super.setup();
		this.getContainerController(); // add client to main
		//Get the JADE runtime interface (singleton)
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		//Create a Profile, where the launch arguments are stored
		try { // add for loop for info array to add all restaurants in distinct new containers!! TODO
			for (RestaurantInfo info : infos) {
				Profile profile = new ProfileImpl();
				profile.setParameter(Profile.CONTAINER_NAME, info.restaurantName + "_container");
				profile.setParameter(Profile.MAIN_HOST, "localhost");
				//create a non-main agent container
				ContainerController container = runtime.createAgentContainer(profile);
		        AgentController ag = container.createNewAgent(info.restaurantName, 
		                                      "domowka4.GateKeeperAgent", 
		                                      new Object[] {info.cuisine, info.restaurantName});//arguments
		        ag.start();
			}
			AgentController ag = getContainerController().createNewAgent("clientAgent", 
                    "domowka4.ClientAgent", 
                    new Object[] { });//arguments
			ag.start();
		} catch (StaleProxyException e) {
		    e.printStackTrace();
		}
	}

	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}
	

}
