package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WemoAPI
{
    private static final String LOG_ID = "[WemoScript] ";

    private static Logger logger = Logger.getLogger("iot-interface");

    /**
     * Configures command line arguments to execute the wemo python script
     * @param deviceIp The ip of the WemoInsight device
     * @param command The command to be sent. Options: turn-off, turn-on, status (not applicable here)
     * @return Array of command line args to execute the script
     */
    private static List<String> setParams(String deviceIp, String command) throws IOException
    {
        logger.info(LOG_ID + "Preparing script");
        List<String> params = new ArrayList<>();
        params.add("bash");
        params.add("run_container.sh");
        params.add(deviceIp);
        params.add(command);
        return params;
    }

    /**
     * Executes the wemo python script and process output
     * @param command The string that is the command's name
     */
    public static String executeScript(String command, String deviceIp){
        try {
            List<String> params = setParams(deviceIp, command);
            List<String> outputs = CommandExecutor.executeCommand(params, ".");

            StringBuilder sb = new StringBuilder();
            for (String s : outputs)
            {
                sb.append(s);
                sb.append("\n");
            }

            return sb.toString();

        } catch (IOException e) {
            logger.severe(LOG_ID + " Error reading response from device " + deviceIp);
            logger.severe(e.getMessage());
            return null;
        }
    }
}
