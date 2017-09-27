package de.felixwolf.masProdSim.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by felix on 21.03.17.
 *
 * Class for the products which are produced in the simulation. Each product consists of the tasks which are necessary
 * to produce it.
 */
public class Product {

    private ArrayList<Task> allTasks = new ArrayList<Task>();
    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

    public Product(ArrayList<Task> allTasks){
        this.allTasks = allTasks;
    }

    public Task getNextTask(){

        for(int i = 0; i < allTasks.size(); i++){
            Task task = allTasks.get(i);
            if(task.isDone()){
                continue;
            }
            else {
                return task;
            }
        }
        //LOGGER.info("Product is finished");
        return null;
    }
}
