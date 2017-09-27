package de.felixwolf.masProdSim.env;

/**
 * Created by felix on 21.03.17.
 *
 *  Tasks are the building blocks for the products. They contain the key information which is required for production:
 *  The machine which is going to fulfill the task and the required time. The tasks also contain additional information
 *  like the total remaining processing time for the product. This data helps the agents to make their decisions.
 *
 */
public class Task {

    private int workTime;
    private int machineId;
    private int followingMachine;
    private boolean isDone;
    private int productId;
    private int taskPosition;
    private double relRemainingTime;
    private double relTimeForNext;

    private boolean isNormalized = false;
    private double [] normalizedProps;


    public Task(int workTime, int machineId, int followingMachine, int productId, double relRemainingTime, double relTimeForNext, int taskPosition, boolean isDone){
        this.workTime = workTime;
        this.machineId = machineId;
        this.isDone = isDone;
        this.productId = productId;
        this.taskPosition = taskPosition;
        this.relRemainingTime = relRemainingTime;
        this.relTimeForNext = relTimeForNext;
        this.followingMachine = followingMachine;
    }


    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getTaskPosition() {
        return taskPosition;
    }

    public void setTaskPosition(int taskPosition) {
        this.taskPosition = taskPosition;
    }

    public double getRelRemainingTime() {
        return relRemainingTime;
    }

    public void setRelRemainingTime(double relRemainingTime) {
        this.relRemainingTime = relRemainingTime;
    }

    public double getRelTimeForNext() {
        return relTimeForNext;
    }

    public void setRelTimeForNext(double relTimeForNext) {
        this.relTimeForNext = relTimeForNext;
    }

    public int getFollowingMachine() {
        return followingMachine;
    }

    public void setFollowingMachine(int followingMachine) {
        this.followingMachine = followingMachine;
    }

    public double [] getNormalizedProperties(){

        if(!isNormalized){

            // normalize
            normalizedProps = new double[4];
            normalizedProps[0] = workTime / 100 * 0.8 + 0.1;
            normalizedProps[1] = relRemainingTime;
            normalizedProps[2] = relTimeForNext;
            normalizedProps[3] = taskPosition / 10 * 0.8 + 0.1;

            isNormalized = true;
        }
        return normalizedProps;
    }
}
