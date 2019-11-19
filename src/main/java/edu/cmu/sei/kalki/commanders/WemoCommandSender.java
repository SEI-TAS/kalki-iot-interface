package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.StageLog;

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

    public static void sendCommands(Device device, List<DeviceCommand> commands, String apiUrl) {
        logger.info("[WemoCommandSender] Sending commands to device: "+device.getId());

        for(DeviceCommand command: commands) {
            switch (command.getName()){
                case "turn-on":
                case "turn-off":
                    sendCommand(device, command);
                    logSendCommand(device, command.getName(), apiUrl);
                    break;
                default:
                    logger.severe("[WemoCommandSender] Command: " + command.getName() + " is not a valid command for a Wemo Insight");
            }
        }
    }

    private static void sendCommand(Device device, DeviceCommand command) {
        args[2] = device.getIp();
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

    private static void logSendCommand(Device device, String command, String apiUrl) {
        logger.info("[WemoCommandSender] Logging that a command was sent to the device.");
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: "+command);
        DeviceControllerApi.sendLog(log, apiUrl);
    }

}
