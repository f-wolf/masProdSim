package de.felixwolf.masProdSim.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * Class to coordinate the production process. The production is simulated in discrete time steps.
 */
public class Production {

    private static final Logger LOGGER = LoggerFactory.getLogger(Production.class);

    private ArrayList<Agent> agents = new ArrayList<>();
    private ArrayList<ProductTemplate> templates = new ArrayList<>();
    private int agentCount;


    public Production(ArrayList<Agent> agents, ArrayList<ProductTemplate> templates){
        this.agents = agents;
        agentCount = agents.size();
        this.templates = templates;
    }

    /**
     * Runs one simulation of the current production setup.
     * @return The total production time
     */

    public int runSimulation(){

        // reset agents' times
        for(Agent agent:agents){
            agent.cleanTempData();
        }

        // prepare products
        ArrayList<Product> products = new ArrayList<>();
        for(ProductTemplate template:templates){
            Product product = template.getProduct();
            products.add(product);
        }

        // reset key times
        int finishedProducts = 0;
        int modelTime = 0;
        int timeToReturn = -1;
        int [] agentsWorkload = new int[agentCount]; // arraylist to keep track of the workload of all agents

        // set initial tasks for agents
        for(int i = 0; i < agentCount; i++) {

            Agent agent = agents.get(i);
            ArrayList<Task> allTasksForAgent = new ArrayList<>();

            for (int p = 0; p < products.size(); p++) {
                Task openTask = products.get(p).getNextTask();

                if (openTask != null) {
                    if (openTask.getMachineId() == i) {
                        allTasksForAgent.add(openTask);
                    }
                }
            }
            agent.initTaskList(allTasksForAgent, agentCount);
            agentsWorkload[i] = allTasksForAgent.size();
        }

        // Preparation is finished. The simulation is started
        boolean jobsTodo = true;
        do{
            for(int i = 0; i < agentCount; i++) {

                Agent agent = agents.get(i);
                int finishTime = agent.getFinishTimeForCurrentJob();

                if(finishTime > modelTime){
                    // agent is already working
                    continue;
                }
                else if (finishTime == modelTime){
                    // agent just finished -> get product -> assign the next task of the product to the corresponding agent
                    agentsWorkload[i]--;
                    Task finishedTask = agent.getCurrentTask();

                    finishedTask.setDone(true);
                    int productId = finishedTask.getProductId();
                    Task nextTask = products.get(productId).getNextTask();

                    if(nextTask == null){
                        // product is done -> all products done? - if yes: production is finished
                        finishedProducts++;
                        if(finishedProducts == products.size()){
                            jobsTodo = false;
                            timeToReturn = modelTime;
                        }
                    }
                    else {
                        int idOfAgentForNextTask = nextTask.getMachineId();
                        Agent reqAgent = agents.get(idOfAgentForNextTask);
                        reqAgent.addTask(nextTask);
                        agentsWorkload[idOfAgentForNextTask]++;
                    }

                    // agent starts work on new task if one is available
                    agent.doNextTask(modelTime, agentsWorkload);
                }
                else if (finishTime == -1){
                    // agent is not working -> try to assign new job
                    agent.doNextTask(modelTime, agentsWorkload);
                }
                else {
                    LOGGER.warn("Unplanned scenario");
                }
            }
            modelTime++;
        }while (jobsTodo);

        for(Agent agent:agents){
            agent.productionEnded(timeToReturn);
        }

        return timeToReturn;
    }
}
