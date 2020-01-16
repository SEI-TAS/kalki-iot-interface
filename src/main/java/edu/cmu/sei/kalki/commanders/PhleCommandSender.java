package edu.cmu.sei.kalki.commanders;

import com.philips.lighting.hue.listener.PHLightListener;
import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.model.*;
import edu.cmu.sei.ttg.kalki.models.StageLog;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PhleCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void sendCommands(Device device, List<DeviceCommand> commands, String apiUrl){
        logger.info("[PhleCommandSender] Sending commands to PHLE: "+device.getId());


        JSONObject lights = getAllLights(device.getIp());
        if(lights == null){
            logger.severe("[PhleCommandSender] Unable to get lights from bridge");
            return;
        }
        Iterator<String> lightIds = lights.keys();
        while(lightIds.hasNext()){
            int id = Integer.parseInt(lightIds.next());
            for(DeviceCommand command: commands){
                switch (command.getName()){
                    case "turn-on":
                        logger.info("[PhleCommandSender] Sending 'turn-on' command to PHLE: " + device.getId());
                        sendIsOn(device.getIp(), id,"true");
                        logSendCommand(device, command.getName(), apiUrl);
                        break;
                    case "turn-off":
                        logger.info("[PhleCommandSender] Sending 'turn-off' command to PHLE: " + device.getId());
                        sendIsOn(device.getIp(), id,"false");
                        logSendCommand(device, command.getName(), apiUrl);
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

    }

    private static void logSendCommand(Device device, String command, String apiUrl) {
        logger.info("[PhleCommandSender] Logging that a command was sent to the device.");
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: "+command);
        DeviceControllerApi.sendLog(log, apiUrl);
    }

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
            logger.severe("[PhleCommandSender] Error sending command to device!");
            logger.severe(e.getMessage());
        }
    }

    private static JSONObject getAllLights(String apiUrl) {
        JSONObject json = null;
        try {
            URL url = new URL("http://"+apiUrl+"/api/newdeveloper/lights");
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
            logger.severe("[PhleCommandSender] Error getting all lights.");
            logger.severe(e.getMessage());
        }
        return json;
    }
}
