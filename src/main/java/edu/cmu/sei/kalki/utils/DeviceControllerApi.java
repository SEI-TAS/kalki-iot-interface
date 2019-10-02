package edu.cmu.sei.kalki.utils;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.StageLog;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class DeviceControllerApi {
    private static final Logger logger = Logger.getLogger("iot-interface");

    private static final String statusEndpoint = "new-status";
    private static final String logEndpoint = "new-stage-log";

    public static void sendStatus(DeviceStatus status, String apiUrl) {
        JSONObject json = new JSONObject(status.toString());
        sendToApi(json, apiUrl+statusEndpoint);
    }

    public static void sendLog(StageLog log, String apiUrl) {
        JSONObject json = new JSONObject(log.toString());
        sendToApi(json, apiUrl+logEndpoint);
    }

    private static void sendToApi(JSONObject object, String apiUrl){
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(object.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[DeviceControllerApi] Error sending object to DeviceController: "+object.toString());
            logger.severe(e.getMessage());
        }
    }
}
