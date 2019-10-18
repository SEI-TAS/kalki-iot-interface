package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.api.*;
import edu.cmu.sei.ttg.kalki.models.Device;

import java.util.logging.Logger;
import java.util.logging.Level;

public class IotInterface {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void main(String[] args) {
        String apiUrl = "10.27.153.3:9090";
        try {
            apiUrl = args[0];
        } catch (ArrayIndexOutOfBoundsException e) { }

        DeviceMonitor monitor = new DeviceMonitor("http://"+apiUrl+"/device-controller-api/");
        logger.info("[IotInterface] DeviceMonitor initialized.");
        ApiServerStartup.start(monitor);
        logger.info("[IotInterface] APIServerStartup started.");
    }
}
