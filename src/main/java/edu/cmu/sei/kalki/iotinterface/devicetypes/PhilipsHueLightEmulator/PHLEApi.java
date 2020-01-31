package edu.cmu.sei.kalki.iotinterface.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.iotinterface.utils.HttpRequest;
import org.json.JSONObject;

import java.util.logging.Logger;

public class PHLEApi
{
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[PHLEApi]";

    private static String getPhleBasePath(String authCode) {
        return "/api/" + authCode + " /lights/";
    }

    /**
     * Makes a get request to the PHLE REST API at the given path
     * @param path The endpoint of the api
     * @return String representation of the response from the API
     */
    public static JSONObject issueCommand(String deviceIp, String authCode, String path, String method, JSONObject payload){
        String targetURL = "http://" + deviceIp + "/api/" + authCode + "/" + path;
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
