package edu.cmu.sei.kalki.utils;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.StageLog;
import org.json.JSONObject;

import java.util.logging.Logger;

public class DeviceControllerApi {
    private static final Logger logger = Logger.getLogger("iot-interface");

    private static final String statusEndpoint = "new-status";
    private static final String logEndpoint = "new-stage-log";

    /**
     * Method to send a device status to the DeviceControllerApi
     * @param status
     * @param apiUrl
     */
    public static void sendStatus(DeviceStatus status, String apiUrl) {
        JSONObject json = new JSONObject(status.toString());
        sendToApi(json, apiUrl+statusEndpoint);
    }

    /**
     * Method to send a Stage Log to the DeviceControllerApi
     * @param log
     * @param apiUrl
     */
    public static void sendLog(StageLog log, String apiUrl) {
        JSONObject json = new JSONObject(log.toString());
        sendToApi(json, apiUrl+logEndpoint);
    }

    /**
     * Helper method to send a json object to the DeviceControllerApi
     * @param object
     * @param apiUrl
     */
    private static void sendToApi(JSONObject object, String apiUrl){
        try {
            HttpRequest.postRequest(object, apiUrl);
        } catch (Exception e) {
            logger.severe("[DeviceControllerApi] Error sending object to DeviceController: "+object.toString());
            logger.severe(e.getMessage());
        }
    }
}
