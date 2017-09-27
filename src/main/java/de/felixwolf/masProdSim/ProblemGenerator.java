package de.felixwolf.masProdSim;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by felix on 23.03.17.
 *
 * This small program can be used to quickly generate new job shop scheduling problems in the desired size.
 * The resulting output has to be saved as a text file and set as input in the settings. Then it will be used for the
 * next simulation run.
 *
 * The structure is the same as for the problems in the OR-library. Each line represents a product. Number pairs
 * represent the different tasks: the first number is the machine id, the second the worktime. All numbers are separated
 * by spaces.
 */

public class ProblemGenerator {

    static int machineCount = 5;
    static int minWorkTime = 12;
    static int maxWorkTime = 70;
    static int products = 10;

    public static void main(String[] args) {

        for(int i = 0; i < products; i++){

            // creation of the randomly sorted machine ids
            ArrayList<Integer> machineIds = new ArrayList<>();
            for(int m = 0; m < machineCount; m++){
                machineIds.add(m);
            }
            Collections.shuffle(machineIds);

            // creation of a new product
            String currentLine = "";
            for(int p = 0; p < machineCount; p++){

                // addition of the machine
                currentLine += machineIds.remove(0).toString() + " ";

                // "creation" and addition of the corresponding worktime
                int worktime = (int) ((Math.random() * (double) (maxWorkTime - minWorkTime)) + minWorkTime);
                currentLine += worktime + " ";
            }
            System.out.println(currentLine);
        }
    }
}
