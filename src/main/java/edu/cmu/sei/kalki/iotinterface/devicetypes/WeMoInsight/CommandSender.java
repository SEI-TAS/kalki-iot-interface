package edu.cmu.sei.kalki.iotinterface.devicetypes.WeMoInsight;

import edu.cmu.sei.kalki.iotinterface.devicetypes.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.List;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[WemoCommandSender]";
    public CommandSender(Device device, List<DeviceCommand> commands) {
        super(device, commands);
    }


    @Override
    protected void sendCommand(DeviceCommand command) {
        switch (command.getName()){
            case "turn-on":
            case "turn-off":
                executeScript(command.getName());
                break;
            default:
                logger.severe(logId + " Command: " + command.getName() + " is not a valid command for a Wemo Insight");
                return;
        }
    }

    /**
     * Configures command line arguments to execute the wemo python script
     * @param deviceIp The ip of the WemoInsight device
     * @param command The command to be sent. Options: turn-off, turn-on, status (not applicable here)
     * @return Array of command line args to execute the script
     */
    private String[] setArgs(String deviceIp, String command){
        return new String[]{
                "python",
                "wemo.py",
                deviceIp,
                command
        };
    }

    /**
     * Executes the wemo python script and process output
     * @param command The string that is the command's name
     */
    private void executeScript(String command){
        try {
            String s = "";
            String[] args = setArgs(device.getIp(), command);
            Process p = Runtime.getRuntime().exec(args);

            logger.info(logId + " Python script executed");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            logger.info(logId + " Input stream captured");

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            logger.info(logId + " Error stream captured");

            logger.info(logId + " Processing input stream");
            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info("[CommandSender] " + s);
            }

            logger.info(logId + " Processing error stream");
            StringBuilder error = new StringBuilder();
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                error.append(s+"\n");
            }
            if(error.toString().length()>0)
                logger.severe(logId + " Error with wemo.py: "+error.toString());

            logSendCommand(command);
        } catch (IOException e) {
            logger.severe(logId + " Error reading response from " + device.getId() + ") " + device.getName());
            logger.severe(e.getMessage());
        }
    }

}
