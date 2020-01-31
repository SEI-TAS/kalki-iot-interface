package edu.cmu.sei.kalki.iotinterface.devicetypes.WeMoInsight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class WemoScript
{
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[WemoScript] ";

    /**
     * Configures command line arguments to execute the wemo python script
     * @param deviceIp The ip of the WemoInsight device
     * @param command The command to be sent. Options: turn-off, turn-on, status (not applicable here)
     * @return Array of command line args to execute the script
     */
    private static String[] setArgs(String deviceIp, String command){
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
    public static String executeScript(String command, String deviceIp){
        try {
            String[] args = setArgs(deviceIp, command);
            Process p = Runtime.getRuntime().exec(args);
            logger.info(logId + " Python script executed");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            logger.info(logId + " Input stream captured");

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            logger.info(logId + " Error stream captured");

            logger.info(logId + " Processing input stream");
            String line;
            StringBuilder fullOutput = new StringBuilder();
            while ((line = stdInput.readLine()) != null) {
                logger.info(logId + line);
                fullOutput.append(line + "\n");
            }

            logger.info(logId + " Processing error stream");
            StringBuilder error = new StringBuilder();
            while ((line = stdError.readLine()) != null) {
                error.append(line + "\n");
            }
            if(error.toString().length()>0) {
                logger.severe(logId + " Error with wemo script: " + error.toString());
            }

            return fullOutput.toString();
        } catch (IOException e) {
            logger.severe(logId + " Error reading response from device " + deviceIp);
            logger.severe(e.getMessage());
            return null;
        }
    }
}
