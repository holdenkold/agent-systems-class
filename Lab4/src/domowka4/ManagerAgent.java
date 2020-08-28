package domowka4;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class ManagerAgent extends Agent{
	
	private static RestaurantInfo[] info = { new RestaurantInfo("polska", "mloda_polka") };

	@Override
	protected void setup() {
		super.setup();
		this.getContainerController(); // add client to main
		//Get the JADE runtime interface (singleton)
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		//Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, "TestContainer");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		//create a non-main agent container
		ContainerController container = runtime.createAgentContainer(profile);
		try { // add for loop for info array to add all restaurants in distinct new containers!! TODO
		        AgentController ag = container.createNewAgent("agentnick", 
		                                      "my.agent.package.AgentClass", 
		                                      new Object[] {});//arguments
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
