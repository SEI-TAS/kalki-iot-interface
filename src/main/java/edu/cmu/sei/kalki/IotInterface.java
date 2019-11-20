package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.Monitors.PhilipsHueLightEmulatorMonitor;
import edu.cmu.sei.kalki.api.*;
import java.util.logging.Logger;

public class IotInterface {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void main(String[] args) {
        String apiUrl = "10.27.153.3:9090";

        try {
            apiUrl = args[0];

        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("[IotInterface] No alternative API IP+port specified. Defaulting to: "+apiUrl);
        }

        DeviceMonitor monitor = new DeviceMonitor("http://"+apiUrl+"/device-controller-api/");
        logger.info("[IotInterface] DeviceMonitor initialized.");

        boolean success = startApiServer(monitor);
        if(!success)
            return;
    }

    private static boolean startApiServer(DeviceMonitor monitor) {
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
