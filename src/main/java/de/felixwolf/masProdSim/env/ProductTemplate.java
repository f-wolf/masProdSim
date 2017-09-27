package de.felixwolf.masProdSim.env;

import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * Template for one product. The product objects are newly created for every simulation run with the help of this class.
 * This is more reliable than reusing product objects from the previous run.
 */

public class ProductTemplate {

    private int [] workTimes;
    private int [] remainingWorkTime;
    private int [] agents;
    private int totalProductionTime;
    private int productId;
    private int numberOfMachines;

    public ProductTemplate(int productId, int [] workTimes, int [] remainingWorkTime, int [] agents, int totalProductionTime){
        this.productId = productId;
        this.workTimes = workTimes;
        this.remainingWorkTime = remainingWorkTime;
        this.agents = agents;
        this.totalProductionTime = totalProductionTime;
        this.numberOfMachines = agents.length;
    }

    /**
     * Method to transform the template data into a new product.
     * @return
     */
    public Product getProduct(){
        // create product
        ArrayList<Task> allTasksForProduct = new ArrayList<Task>();
        for(int i = 0; i < numberOfMachines; i++){

            int machineId = agents[i];
            int workTime = workTimes[i];
            double relRemainingTime = (double) remainingWorkTime[i] / (double) totalProductionTime;

            double relTimeForNext = 0.0;
            if(i < numberOfMachines - 1) {
                relTimeForNext = (double) workTimes[i + 1] / (double) remainingWorkTime[i];
            }

            int nextMachineId = -1;
            if(i < numberOfMachines - 1){
                nextMachineId = agents[i + 1];
            }

            Task task = new Task(workTime, machineId, nextMachineId, productId, relRemainingTime, relTimeForNext, i, false);
            allTasksForProduct.add(task);
        }
        Product product = new Product(allTasksForProduct);

        return product;
    }
}
