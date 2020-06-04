package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.kalki.iotinterface.app.api.*;
import edu.cmu.sei.kalki.db.utils.Config;

import java.io.IOException;
import java.util.logging.Logger;

public class IotInterface {
    private static final Logger logger = Logger.getLogger("iot-interface");

    public static void main(String[] args) {
        try {
            Config.load("config.json");
        } catch (IOException e) {
            logger.severe("[IotInterface] Error parsing the config.json. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        }

        MonitorManager monitor = new MonitorManager();
        logger.info("[IotInterface] MonitorManager initialized.");

        if(args.length >= 2 && args[0].equals("test"))
        {
            IntegrationTests.runTest(args[1], monitor);
        }
        else {
            if (!startApiServer(monitor)) {
                logger.info("[IotInterface] APIServerStartup failed. Exiting.");
                System.exit(-1);
            }
        }
    }

    private static boolean startApiServer(MonitorManager monitor) {
        boolean success = false;
        int attempts = 0;

        while (attempts < 10 && !success) {
            try {
                ApiServerStartup.start(monitor);
                logger.info("[IotInterface] APIServerStartup started.");
                success = true;
            } catch (Exception e) {
                sleep(5000);
                attempts++;
                logger.severe("[IotInterface] Attempt "+attempts+" at starting server.");
            }
        }

        return success;
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.severe("[IotInterface] Error attempting to sleep");
        }
    }
}
