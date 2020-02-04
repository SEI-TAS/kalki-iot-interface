package edu.cmu.sei.kalki.iotinterface.app;
import edu.cmu.sei.kalki.iotinterface.app.api.*;
import edu.cmu.sei.kalki.iotinterface.common.utils.Config;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IotInterface {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void main(String[] args) {
        commandSenderTest();

        try {
            Config.load("config.json");
        } catch (IOException e) {
            logger.severe("[IotInterface] Error parsing the config.json. Exiting.");
            e.printStackTrace();
            System.exit(-1);
        }

        MonitorManager monitor = new MonitorManager();
        logger.info("[IotInterface] MonitorManager initialized.");

        if(!startApiServer(monitor)){
            logger.info("[IotInterface] APIServerStartup failed. Exiting.");
            System.exit(-1);
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

    private static void commandSenderTest() {
        // Test
        DeviceType deviceType = new DeviceType();
        //deviceType.setName("WeMo Insight");
        deviceType.setName("Philips Hue Light Emulator");
        Device device = new Device();
        device.setId(1);
        device.setIp("127.0.0.1");
        device.setType(deviceType);
        List<DeviceCommand> commands = new ArrayList<>();
        DeviceCommand command = new DeviceCommand();
        command.setName("turn-on");
        commands.add(command);
        CommandManager.processCommands(device, commands);
    }

}
