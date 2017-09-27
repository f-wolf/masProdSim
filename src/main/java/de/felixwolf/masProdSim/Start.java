package de.felixwolf.masProdSim;

import de.felixwolf.masProdSim.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by felix on 21.03.17.
 *
 * Class to start the simulation
 */

public class Start {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Start.class);

        Environment theEnvironment = new Environment();
        theEnvironment.setup();

        int runtime = theEnvironment.runSimulation();
        logger.info("The best runtime was " + runtime);
    }
}
