package de.felixwolf.masProdSim.env;

import de.felixwolf.masProdSim.machine.Fifo;
import de.felixwolf.masProdSim.machine.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * The agents represent the machines in the production network and control their actions.
 */
public class Agent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    // id and capability are not needed in the current scenario, but very helpful for more complex ones
    private int id;
    private int capabilityId;
    private Scheduler scheduler;

    private int currentFinishTime = -1;
    private Task currentTask = null;

    public Agent(int id, int capability, String schedulerType){
        this.id = id;
        this.capabilityId = capability;

        try {
            Class<?> schedulerClass = Class.forName("de.felixwolf.masProdSim.machine." + schedulerType);
            Constructor<?> constructor = schedulerClass.getConstructors()[0];
            scheduler = (Scheduler) constructor.newInstance();

        }catch (Exception e){
            LOGGER.warn("Scheduler could not be created. Using FiFo scheduler as default");
            e.printStackTrace();
            scheduler = new Fifo();
        }
    }

    /**
     * Removes the data from the last production run
     */
    public void cleanTempData(){
        currentFinishTime = -1;
        currentTask = null;
        scheduler.cleanTempData();
    }

    public void initTaskList(ArrayList<Task> firstTasks, int agentCount){
        scheduler.init(firstTasks, id, agentCount);
    }

    public void addTask(Task task){
        scheduler.addTask(task);
    }

    /**
     * This method is called whenever the agent is not working. The agent is aware of all tasks which are available for
     * it (the tasks where given to the agent via the methods initTaskList(...) and addTask(...). The agent can choose
     * freely which task it is going to do next. The decision logic is managed by the scheduler.
     * @param currentTime
     * @param agentsWorkLoad
     */
    public void doNextTask(int currentTime, int [] agentsWorkLoad){

        if(!scheduler.tasksToDo()){
            currentTask = null;
            currentFinishTime = -1;
            return;
        }

        Task taskToDo = scheduler.getNextTask(agentsWorkLoad, currentTime);
        int workTime = taskToDo.getWorkTime();

        currentTask = taskToDo;
        currentFinishTime = currentTime + workTime;

    }

    public int getFinishTimeForCurrentJob(){
        return currentFinishTime;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void productionEnded(int endOfProductionTime){

        scheduler.setLastRunTime(endOfProductionTime);

        currentFinishTime =-1;
        currentTask = null;
    }

}
