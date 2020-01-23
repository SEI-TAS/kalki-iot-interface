package edu.cmu.sei.kalki.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.utils.HttpRequest;
import edu.cmu.sei.kalki.utils.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private JSONObject lights;

    public CommandSender(Device device, List<DeviceCommand> commands) {
        super(device, commands);
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
                    logger.info("[PhleCommandSender] Sending 'turn-on' command to PHLE: " + device.getId());
                    sendIsOn(device.getIp(), id,"true");
                    logSendCommand(command.getName());
                    break;
                case "turn-off":
                    logger.info("[PhleCommandSender] Sending 'turn-off' command to PHLE: " + device.getId());
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
                    logger.severe("[PhleCommandSender] Command: " + command.getName() + " not supported for Phillips Hue Light Emulator.");
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
            JSONObject body = new JSONObject("{\"on\":"+isOn+"}");
            String apiUrl = "http://"+ip+"/api/newdeveloper/lights/"+lightId+"/state";
            HttpRequest.putRequest(body, apiUrl);
        } catch (Exception e) {
            logger.severe("[PhleCommandSender] Error sending command to device!");
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
            json = HttpRequest.getRequest("http://"+ip+"/api/newdeveloper/lights");
        } catch (Exception e) {
            logger.severe("[PhleCommandSender] Error getting all lights.");
            logger.severe(e.getMessage());
        }
        return json;
    }
}
