package de.felixwolf.masProdSim.machine;

import de.felixwolf.masProdSim.env.Config;
import de.felixwolf.masProdSim.env.Task;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by felix on 23.03.17.
 *
 * Scheduler which uses Evolution strategy powered reinforcement learning
 * Inspired by the OpenAI post: https://blog.openai.com/evolution-strategies/
 *
 */

public class EvolutionStrategy implements Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvolutionStrategy.class);

    private ArrayList<Task> allTasks = new ArrayList<>();                       // List of all tasks the agent can currently do
    private int agentId;
    private int agentCount;

    // Variable settings for the evolution strategy. Taken from the properties file
    private int populations = Config.getIntegerProperty("es.populations", 2);   // number of populations
    private double sigma = Config.getDoubleProperty("es.sigma", 0.1);           // noise standard deviation
    private double alpha = Config.getDoubleProperty("es.alpha", 0.01);          // learning rate
    private int[] hiddenLayers = Config.getIntArrayProperty("es.layerConfig", new int[]{5, 4}); // sizes of the hidden layers

    // Derived variables
    private int inputVectorLength = 5;
    private int episode = 0;
    private int populationSize = calcPopulationSize(inputVectorLength);
    private int[] allLayers = prepareAllLayers();
    private INDArray w = Nd4j.randn(1, populationSize);
    private INDArray N = Nd4j.randn(populations, populationSize);
    private INDArray R = Nd4j.zeros(populations);

    @Override
    public void init(ArrayList<Task> initTasks, int agentId, int agentCount) {

        this.agentId = agentId;
        this.agentCount = agentCount;

        if(episode % populations == 0){
            N = Nd4j.randn(populations, populationSize);
            R = Nd4j.zeros(populations);
        }

        allTasks = initTasks;
    }

    @Override
    public void addTask(Task newTask) {
        allTasks.add(newTask);
    }

    /**
     * This method is called whenever the agent is not working and tasks are available.
     * @param agentsWorkLoad
     * @param currentTime
     * @return
     */
    @Override
    public Task getNextTask(int[] agentsWorkLoad, int currentTime) {

        if(allTasks.size() == 1){
            return allTasks.remove(0);
        }

        double highestUrgency = Double.MAX_VALUE * (-1);
        int mostUrgentTask = 0;

        for(int i = 0; i < allTasks.size(); i++){
            Task aTask = allTasks.get(i);

            INDArray taskInput = prepareInputVector(aTask, agentsWorkLoad, currentTime);
            INDArray w_try = w.add(N.getRow(episode % populations).mul(sigma));
            double urgency = calculateUrgency(taskInput, w_try);

            if(urgency > highestUrgency){
                highestUrgency = urgency;
                mostUrgentTask = i;
            }
        }

        return allTasks.remove(mostUrgentTask);
    }

    /**
     * Returns true if the agent has open tasks
     * @return
     */
    @Override
    public boolean tasksToDo() {
        if (allTasks.size() > 0){
            return true;
        }
        return false;
    }

    /**
     * After each simulation run, the environment informs the agents about the last run time. This information is used
     * as training data to improve the scheduler.
     * @param runTime
     */
    @Override
    public void setLastRunTime(int runTime) {

        double error = error = Math.pow(runTime / 1000.0, 3.0) / 10 * (-1);

        R.putScalar(episode % populations, error);

        if(episode % populations == populations - 1){

            if(R.stdNumber().doubleValue() == 0.0){
                //System.err.println("No standard deviation left -> random values");
                R = Nd4j.randn(1, populations);
                R = R.mul(10);
            }

            INDArray A = (R.sub(R.meanNumber())).div(R.stdNumber());
            w = w.add((((N.transpose()).mmul(A.transpose())).mul((alpha /(populations * sigma)))).transpose());
        }

        episode++;
    }

    // todo
    @Override
    public void saveData() {

    }

    @Override
    public void cleanTempData() {
        allTasks.clear();
    }

    /**
     * Method to calculate the population size. In this case, the populations are the weights of the artificial neural
     * network which is used in the function "calculateUrgency" to rank the tasks.
     * @param inputLength
     * @return
     */
    private int calcPopulationSize(int inputLength){

        int sum = 0;
        sum += inputLength * hiddenLayers[0];

        for(int i = 0; i < hiddenLayers.length - 1; i++) {
            sum += hiddenLayers[i] * hiddenLayers[i + 1];
        }
        sum += hiddenLayers[hiddenLayers.length-1];

        return sum;
    }

    /**
     * This method creates the input vector for the urgency calculation.
     * @param aTask
     * @param agentsWorkLoad
     * @param currentTime
     * @return
     */
    private INDArray prepareInputVector(Task aTask, int[] agentsWorkLoad, int currentTime){

        double [] taskProperties = aTask.getNormalizedProperties();
        int idOfNextMachine = aTask.getFollowingMachine();
        int workloadAtNextMachine = 0;
        if(idOfNextMachine >= 0 && idOfNextMachine < agentsWorkLoad.length){
            workloadAtNextMachine = agentsWorkLoad[idOfNextMachine];
        }
        double [] normalizedWorkloadNM = {Math.min(((double) workloadAtNextMachine / 10.0 * 0.8 + 0.1), 0.9)};

        // create one with size taskProperties + 1
        INDArray taskInput = Nd4j.zeros(1,taskProperties.length + 1);
        taskInput.put(new INDArrayIndex[]{NDArrayIndex.all(), NDArrayIndex.interval(0,taskProperties.length)}, Nd4j.create(taskProperties));
        taskInput.put(taskProperties.length, Nd4j.create(normalizedWorkloadNM));

        /*
        Two possible additional features: workload of this agent and the current time.
        Adding them requires changing the size of "taskInput"

        int workloadThisMachine = allTasks.size();
        double [] normalizedWorkloadTM = {Math.min(((double) workloadThisMachine / 10.0 * 0.8 + 0.1), 0.9)};
        double [] normalizedCurrentTime = {Math.min(((double) currentTime / 1200.0 * 0.8 + 0.1), 0.9)};
        taskInput.put(taskProperties.length + 1, Nd4j.create(normalizedWorkloadTM));
        taskInput.put(taskProperties.length + 2, Nd4j.create(normalizedCurrentTime));
         */
        //LOGGER.debug("The task input: " + taskInput.toString());

        if(taskInput.size(1) != inputVectorLength){
            LOGGER.warn("The size of the input vector is not as expected");
        }

        return taskInput;
    }

    /** Method to calculate the urgency. The matrix multiplications represent an artificial neural network with a linear
     * activation function.
     * @param input
     * @param weights
     * @return
     */
    double calculateUrgency(INDArray input, INDArray weights){

        // 1. verify that the inputs make sense
        int givenWeightsNum = weights.size(1);
        //LOGGER.debug("No of given weights: " + givenWeightsNum);

        int requiredWeightsNum = 0;

        for(int i = 0; i < allLayers.length - 1; i++){
            requiredWeightsNum += allLayers[i] * allLayers[i + 1];
        }

        if(requiredWeightsNum != givenWeightsNum){
            LOGGER.warn("Number of weights and desired configuration for hidden layers does not match!");
            return 0;
        }

        // 2 Multiply the input with all layers
        int lastIntervalEnd = 0;
        INDArray result = input;
        for(int i = 0; i < allLayers.length - 1; i++){

            int layerWeightsCount = allLayers[i] * allLayers[i + 1];
            int currentIntervalEnd = lastIntervalEnd + layerWeightsCount;
            INDArray rawLayer = weights.get(NDArrayIndex.all(), NDArrayIndex.interval(lastIntervalEnd,currentIntervalEnd));
            lastIntervalEnd = currentIntervalEnd;

            INDArray layer = rawLayer.reshape(allLayers[i], allLayers[i + 1]);
            result = result.mmul(layer);
        }

        double urgency = result.getDouble(0,0);

        return urgency;
    }

    /**
     * Function to create an array with the sizes of all the layers of the artificial neural network. Letting the user
     * only choose the hidden layers avoids potential errors (like a mismatch between the first layer and the number of
     * input values).
     * @return
     */
    private int[] prepareAllLayers(){

        int [] allLayerSizes = new int[hiddenLayers.length + 2];
        allLayerSizes[0] = inputVectorLength;                                               // layer for input
        allLayerSizes[allLayerSizes.length - 1] = 1;                                        // output layer
        System.arraycopy(hiddenLayers, 0, allLayerSizes, 1, hiddenLayers.length);

        return allLayerSizes;
    }

}
