package edu.cmu.sei.kalki.commanders;

import com.philips.lighting.hue.listener.PHLightListener;
import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.model.*;
import edu.cmu.sei.ttg.kalki.models.StageLog;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PhleCommandSender {
    private static PHHueSDK phHueSDK;
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void sendCommands(Device device, List<DeviceCommand> commands, String apiUrl){
        logger.info("[PhleCommandSender] Sending commands to PHLE: "+device.getId());
        connectToHue(device);

        PHBridge bridge = null;
        for (int i=0; i < 10 && bridge == null; i++){
            logger.info("Waiting to discover PHLE bridge...");
            try {Thread.sleep(100);}catch (Exception e) { }
            bridge = phHueSDK.getSelectedBridge();
        }
        if(bridge == null){
            logger.severe("Unable to connect to PHLE bridge & send commands to device: "+device.getId());
            return;
        }
        PHBridgeResourcesCache cache = bridge.getResourceCache();
        List<PHLight> lights = cache.getAllLights();
        for(int i=0;i< lights.size();i++){
            for(DeviceCommand command: commands){
                switch (command.getName()){
                    case "turn-on":
                        logger.info("[PhleCommandSender] Sending 'turn-on' command to PHLE: " + device.getId());
                        sendIsOn(device.getIp(), i+1,"true");
                        logSendCommand(device, command.getName(), apiUrl);
                        break;
                    case "turn-off":
                        logger.info("[PhleCommandSender] Sending 'turn-off' command to PHLE: " + device.getId());
                        sendIsOn(device.getIp(), i+1,"false");
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
            URL url = new URL("http://"+ip+"/api/newdeveloper/lights/"+lightId+"/state/");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            JSONObject json = new JSONObject("{\"on\":"+isOn+"}");
            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[PhleCommandSender] Error sending command to device!");
            logger.severe(e.getMessage());
        }
    }

    private static void connectToHue(Device device) {
        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.getNotificationManager().registerSDKListener(listener);

        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(device.getIp());
        accessPoint.setUsername("newdeveloper");
        phHueSDK.connect(accessPoint);
    }

    private static PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            logger.info("Found AccessPoints!");
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            // Start the Pushlink Authentication.
            logger.info("AuthenticationRequired");
            phHueSDK.startPushlinkAuthentication(accessPoint);
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            logger.info("Connected to bridge");
/*            String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            logger.info("IP is : " + lastIpAddress);
            logger.info("Username is: " + username);*/
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {
            if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                logger.severe("Bridge not responding: " + message);
            }
            else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                logger.severe("Button not pressed: " + message);
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                logger.severe("Authentication failed: " + message);
            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                logger.severe("Bridge not found: " + message);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError: parsingErrorsList) {
                logger.severe("ParsingError : " + parsingError.getMessage());
            }
        }
    };
}
