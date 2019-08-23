package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.api.*;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.database.Postgres;

import java.util.logging.Logger;
import java.util.logging.Level;

public class IotInterface {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void main(String[] args) {
        Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");
//        Postgres.setLoggingLevel(Level.OFF);

        DeviceMonitor monitor = new DeviceMonitor();
        logger.info("[IotInterface] DeviceMonitor initialized.");
        ApiServerStartup.start(monitor);
        logger.info("[IotInterface] APIServerStartup started.");
    }
}
