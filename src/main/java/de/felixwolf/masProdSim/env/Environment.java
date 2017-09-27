package de.felixwolf.masProdSim.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by felix on 21.03.17.
 *
 * This is the general environment where all properties of the production simulation are prepared and stored
 *
 */
public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private ArrayList<ProductTemplate> allProductTemplates = new ArrayList<>();
    private ArrayList<Agent> allAgents = new ArrayList<>();
    private Production production;

    /**
     * Preparation of the whole environment
     */
    public void setup(){

        // read problem instance
        List<String> lines = readInstance();
        ArrayList<Integer> requiredMachines = prepareProductTemplates(lines);
        initializeMachines(requiredMachines);

        production = new Production(allAgents, allProductTemplates);
        LOGGER.info("Environment is set up");
    }

    public int runSimulation(){

        String runMode = Config.getProperty("runMode", "single");

        switch (runMode){
            case "single": return singleSimulationRun();
            case "multiple": return multipleSimulationRuns();
            default: LOGGER.warn("Unknown run mode. 'Single' is selected!"); return singleSimulationRun();
        }
    }

    /**
     * One simulation run
     * @return time of total production
     */
    public int singleSimulationRun(){

        return production.runSimulation();
    }

    /**
     * Multiple simulation runs as training for machine learning techniques
     * @return
     */
    public int multipleSimulationRuns(){

        int bestRuntime = Integer.MAX_VALUE;

        for(int i = 0; i < 100000; i++){
            int runtime = production.runSimulation();

            if(runtime < bestRuntime){
                bestRuntime = runtime;
                LOGGER.info("new best runtime at run " + i + ": " + bestRuntime);
            }

            if(i%10000 == 0) {
                LOGGER.debug("Run " + i + " finished: " + runtime);
            }
        }
        return bestRuntime;
    }

    /**
     * Reads the job shop machine instance as specified in the properties
     * @return
     */

    private List<String> readInstance(){

        List<String> lines = null;

        String defaultPath = "src/main/resources/default.txt";
        String inputPath = Config.getProperty("jobFilePath", defaultPath);
        Path path = Paths.get(inputPath);

        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {

            LOGGER.warn("Desired file was not found. Reading default path");

            path = Paths.get(defaultPath);
            try {
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return lines;
    }

    /**
     * Preparation of the templates of the products which are to be produced.
     * @param lines: The raw lines of the problem instance
     * @return the required machines are returned
     */
    private ArrayList<Integer> prepareProductTemplates(List<String> lines){

        ArrayList<Integer> requiredMachines = new ArrayList<>();

        int lineCount = 0;
        for (String line : lines) {

            // cleaning of string: 1) removal of leading and trailing spaces
            line = line.trim();
            // removal of multiple spaces
            line = line.replaceAll(" +", " ");
            // removal of all non numeric characters
            line = line.replaceAll("[^\\d ]", "");

            String [] lineSplitted = line.split(" ");

            if(lineSplitted.length % 2 != 0){
                LOGGER.warn("Bad input: Number of machines and worktimes not equal in line " + lineCount);
            }
            int numberOfMachines = lineSplitted.length / 2;

            // creation of arrays with machine data
            int [] workTimes = new int[numberOfMachines];
            int [] remainingWorkTime = new int[numberOfMachines];
            int [] machines = new int[numberOfMachines];
            int totalProductionTime = 0;

            for(int i = numberOfMachines - 1; i >= 0; i--){
                int machineId = Integer.parseInt(lineSplitted[i * 2]);
                machines[i] = machineId;
                if(!requiredMachines.contains(machineId)){
                    requiredMachines.add(machineId);
                }

                workTimes[i] = Integer.parseInt(lineSplitted[i * 2 + 1]);
                totalProductionTime += workTimes[i];

                remainingWorkTime[i] = workTimes[i];
                if(i < numberOfMachines - 1){
                    remainingWorkTime[i] += remainingWorkTime[i+1];
                }
            }

            ProductTemplate template = new ProductTemplate(lineCount, workTimes, remainingWorkTime, machines, totalProductionTime);
            allProductTemplates.add(template);

            lineCount++;
        }
        return requiredMachines;
    }

    private void initializeMachines(ArrayList<Integer> requiredMachines){

        String schedulerType = Config.getProperty("scheduler", "Fifo");
        Collections.sort(requiredMachines);

        //for(int machineId:requiredMachines){
        for(int i = 0; i < requiredMachines.size(); i++){
            int machineId = requiredMachines.get(i);

            if(i != machineId){
                // machine ids are not consecutive or not starting from 0. To avoid later errors the program will end
                LOGGER.error("Machine ids have to be consecutive and starting from zero.");
                System.exit(1);
            }

            Agent agent = new Agent(machineId, machineId, schedulerType); // The machineId is also the capabilityId in these cases.
            allAgents.add(agent);
        }
    }
}
