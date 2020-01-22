package edu.cmu.sei.kalki.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.utils.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private JSONObject lights;

    public CommandSender(Device device, List<DeviceCommand> commands, String apiUrl) {
        super(device, commands, apiUrl);
        lights = getAllLights(device.getIp());
    }

    /**
     * Sends commands to each light associated with the given PHLE bridge
     * @param command Currently supports turn-on & turn-off
     */
    @Override
    protected void sendCommand(DeviceCommand command) {
        logger.info("[PhleCommandSender] Sending commands to PHLE: "+device.getId());

        if(lights== null){
            logger.severe("[PhleCommandSender] Unable to get lights from bridge");
            return;
        }
        Iterator<String> lightIds = lights.keys();
        while(lightIds.hasNext()){
            int id = Integer.parseInt(lightIds.next());
            switch (command.getName()){
                case "turn-on":
                    logger.info("[CommandSender] Sending 'turn-on' command to PHLE: " + device.getId());
                    sendIsOn(device.getIp(), id,"true");
                    logSendCommand(command.getName());
                    break;
                case "turn-off":
                    logger.info("[CommandSender] Sending 'turn-off' command to PHLE: " + device.getId());
                    sendIsOn(device.getIp(), id,"false");
                    logSendCommand(command.getName());
                    break;
                case "set-name":
                case "set-brightness":
                case "set-color":
                case "set-schedule":
                case "set-group":
                case "set-scene":
                default:
                    logger.severe("[CommandSender] Command: " + command.getName() + " not supported for Phillips Hue Light Emulator.");
            }
        }
    }

    /**
     * Sets the light's state 'isOn' property
     * @param ip The ip of the bridge
     * @param lightId The id of the light on the bridge
     * @param isOn String value of a boolean determining 'isOn'
     */
    private static void sendIsOn(String ip, int lightId, String isOn) {
        try {
            URL url = new URL("http://"+ip+"/api/newdeveloper/lights/"+lightId+"/state");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            JSONObject json = new JSONObject("{\"on\":"+isOn+"}");
            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
            httpCon.disconnect();
        } catch (Exception e) {
            logger.severe("[CommandSender] Error sending command to device!");
            logger.severe(e.getMessage());
        }
    }

    /**
     * Get all the lights associated with the PHLE bridge
     * @param ip The ip of the PHLE bridge
     * @return JSON object representing all lights connected to the bridge
     */
    private static JSONObject getAllLights(String ip) {
        JSONObject json = null;
        try {
            URL url = new URL("http://"+ip+"/api/newdeveloper/lights");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line = "";
            while((line = in.readLine()) != null) {
                response.append(line);
            }
            logger.info(response.toString());
            json = new JSONObject(response.toString());
            httpCon.disconnect();
        } catch (Exception e) {
            logger.severe("[CommandSender] Error getting all lights.");
            logger.severe(e.getMessage());
        }
        return json;
    }
}
