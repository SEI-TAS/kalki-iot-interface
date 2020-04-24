package edu.cmu.sei.kalki.iotinterface.common;

import edu.cmu.sei.kalki.iotinterface.common.utils.HttpRequest;
import edu.cmu.sei.kalki.iotinterface.common.utils.Config;
import edu.cmu.sei.kalki.db.models.DeviceStatus;
import edu.cmu.sei.kalki.db.models.StageLog;
import org.json.JSONObject;

import java.util.logging.Logger;

public class DeviceControllerApi {
    private static final Logger logger = Logger.getLogger("iot-interface");

    private static final String apiIp = Config.data.get("device_controller_api_ip");
    private static final String apiPort = Config.data.get("device_controller_api_port");
    private static final String basePath = "/device-controller-api";

    private static final String apiUrl = "http://"+apiIp+":"+apiPort+basePath;
    private static final String statusPath = "/new-status";
    private static final String logPath = "/new-stage-log";

    /**
     * Method to send a device status to the DeviceControllerApi
     * @param status
     */
    public static void sendStatus(DeviceStatus status) {
        JSONObject json = new JSONObject(status.toString());
        sendToApi(json, apiUrl+statusPath);
    }

    /**
     * Method to send a Stage Log to the DeviceControllerApi
     * @param log
     */
    public static void sendLog(StageLog log) {
        JSONObject json = new JSONObject(log.toString());
        sendToApi(json, apiUrl+logPath);
    }

    /**
     * Helper method to send a json object to the DeviceControllerApi
     * @param object
     * @param endpoint
     */
    private static void sendToApi(JSONObject object, String endpoint){
        try {
            HttpRequest.postRequest(object, endpoint);
        } catch (Exception e) {
            logger.severe("[DeviceControllerApi] Error sending object to DeviceController: "+object.toString());
            logger.severe(e.getMessage());
        }
    }
}
