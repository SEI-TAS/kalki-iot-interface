package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class WeMoInsightCommandSender extends DeviceCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static String[] args = new String[]{
            "python",
            "wemo.py",
            "device",
            "command"
    };

    @Override
    public void sendCommands() {
        logger.info("[WeMoInsightCommandSender] Sending commands to device: "+device.getId());

        for(DeviceCommand command: commands) {
            switch (command.getName()){
                case "turn-on":
                case "turn-off":
                    sendCommand(device, command);
                    logSendCommand(command.getName());
                    break;
                default:
                    logger.severe("[WeMoInsightCommandSender] Command: " + command.getName() + " is not a valid command for a Wemo Insight");
            }
        }
    }

    private static void sendCommand(Device device, DeviceCommand command) {
        args[2] = device.getIp();
        args[3] = command.getName();
        String s = null;

        try {
            Process p = Runtime.getRuntime().exec(args);

            logger.info("[WeMoInsightCommandSender] Python script executed");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            logger.info("[WeMoInsightCommandSender] Input stream captured");

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            logger.info("[WeMoInsightCommandSender] Error stream captured");

            logger.info("[WeMoInsightCommandSender] Processing input stream");
            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info("[WeMoInsightCommandSender] " + s);
            }

            logger.info("[WeMoInsightCommandSender] Processing error stream");
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                logger.severe("[WeMoInsightCommandSender] Error with wemo.py: "+s);
            }
        } catch (IOException e) {
            logger.severe("[WeMoInsightCommandSender] Error reading response from " + device.getId() + ") " + device.getName());
            logger.severe(e.getMessage());
        }
    }

}
