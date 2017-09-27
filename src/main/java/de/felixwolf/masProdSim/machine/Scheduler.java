package de.felixwolf.masProdSim.machine;

import de.felixwolf.masProdSim.env.Task;

import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * Interface for all schedulers
 */
public interface Scheduler {

    public void init(ArrayList<Task> initTasks, int agentId, int agentCount);

    public void addTask(Task newTask);

    public Task getNextTask(int[] agentsWorkLoad, int currentTime);

    public boolean tasksToDo();

    public void setLastRunTime(int runTime);


    public void saveData();
    public void cleanTempData();

}
