package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

public class WemoCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static String[] args = new String[]{
            "python",
            "wemo.py",
            "device",
            "command"
    };

    public static void sendCommands(Device device, List<DeviceCommand> commands) {
        logger.info("[WemoCommandSender] Sending commands to device: "+device.getId());

        for(DeviceCommand command: commands) {
            switch (command.getName()){
                case "turn-on":
                case "turn-off":
                    sendCommand(device, command);
                    break;
                default:
                    logger.severe("[WemoCommandSender] Command: " + command.getName() + " is not a valid command for a Wemo Insight");
            }
        }
    }

    private static void sendCommand(Device device, DeviceCommand command) {
        args[2] = device.getName();
        args[3] = command.getName();
        String s = null;

        try {
            Process p = Runtime.getRuntime().exec(args);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info("[WemoCommandSender] " + s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                logger.severe("[WemoCommandSender] Error with wemo.py: "+s);
            }
        } catch (IOException e) {
            logger.severe("[WemoCommandSender] Error reading response from " + device.getId() + ") " + device.getName());
            logger.severe(e.getMessage());
        }
    }

}
