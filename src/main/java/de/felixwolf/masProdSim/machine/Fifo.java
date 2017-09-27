package de.felixwolf.masProdSim.machine;

import de.felixwolf.masProdSim.env.Task;
import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * This scheduler simply returns the first element of the tasks list for production.
 */
public class Fifo implements Scheduler {

    private ArrayList<Task> allTasks = new ArrayList<>();
    int agentId;

    @Override
    public void init(ArrayList<Task> initTasks, int agentId, int agentCount) {
        this.agentId = agentId;
        allTasks = initTasks;
    }

    @Override
    public void addTask(Task newTask) {
        allTasks.add(newTask);
    }

    @Override
    public Task getNextTask(int[] agentsWorkLoad, int currentTime) {

        if(allTasks.size() > 0){
            return allTasks.remove(0);              // returns the first task of list
        }
        else {
            return null;
        }
    }


    @Override
    public boolean tasksToDo() {
        if (allTasks.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public void setLastRunTime(int runTime) {
        // not useful for FiFo
    }

    @Override
    public void saveData() {
        // not useful for Fifo
    }

    @Override
    public void cleanTempData() {
        allTasks.clear();
    }
}
