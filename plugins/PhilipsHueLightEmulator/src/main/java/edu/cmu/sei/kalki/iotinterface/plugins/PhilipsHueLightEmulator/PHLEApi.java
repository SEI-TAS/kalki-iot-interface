package edu.cmu.sei.kalki.iotinterface.plugins.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.iotinterface.common.utils.HttpRequest;
import org.json.JSONObject;

import java.util.logging.Logger;

public class PHLEApi
{
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[PHLEApi]";

    private static String getPhleBasePath(String authCode) {
        return "/api/" + authCode + " /lights";
    }

    /**
     * Sets the light's state 'isOn' property
     * @param ip The ip of the bridge
     * @param lightId The id of the light on the bridge
     * @param isOn String value of a boolean determining 'isOn'
     */
    public static void sendIsOn(String ip, String authCode, int lightId, String isOn) {
        JSONObject body = new JSONObject("{\"on\":" + isOn + "}");
        PHLEApi.issueCommand(ip, authCode, "" + lightId + "/state", "PUT", body);
    }

    /**
     * Get all the lights associated with the PHLE bridge
     * @param ip The ip of the PHLE bridge
     * @return JSON object representing all lights connected to the bridge
     */
    public static JSONObject getAllLights(String ip, String authCode) {
        return PHLEApi.issueCommand(ip, authCode, "", "GET", null);
    }

    /**
     * Makes a get request to the PHLE REST API at the given path
     * @param path The endpoint of the api
     * @return String representation of the response from the API
     */
    public static JSONObject issueCommand(String deviceIp, String authCode, String path, String method, JSONObject payload){
        String targetURL = "http://" + deviceIp + getPhleBasePath(authCode) + "/" + path;
        logger.info("Sending command: " + targetURL);
        try{
            if("GET".equals(method)) {
                return HttpRequest.getRequest(targetURL);
            }
            else if("PUT".equals(method)) {
                HttpRequest.putRequest(payload, targetURL);
                return null;
            }
            else {
                return new JSONObject("{\"error\": \"Method not supported: " + method + "\"}");
            }
        } catch (Exception e) {
            logger.severe(logId + " Error getting a response from the bridge: "+ e);
            return new JSONObject("{\"error\": \"Error: " + e.toString() + "\"}");
        }
    }
}
