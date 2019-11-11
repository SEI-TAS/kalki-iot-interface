package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class WeMoInsightMonitor extends PollingMonitor {

    private String deviceIp;
    private Boolean isOn;
    private int deviceId;

    private Map<String, String> attributes;
    private DeviceStatus status;

    public WeMoInsightMonitor(int deviceId, String ip, int samplingRate, String url){
        this.deviceIp = ip;
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.apiUrl = url;
        logger.info("[WeMoInsightMonitor] Starting monitor.");
        start();
    }

    @Override
    public void pollDevice() {
        attributes = new HashMap<String, String>();
        try {

            // run the command
            // using the Runtime exec method:
            String[] args = new String[]{
                    "python",
                    "wemo.py",
                    deviceIp,
                    "status"
            };
            Process p = Runtime.getRuntime().exec(args);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            StringBuilder st = new StringBuilder();
	    String s = null;
	    while ((s = stdInput.readLine()) != null) {
		st.append(s);
                //s = s.replace("Switch: " + deviceIp, "");
                //isOn = s.contains("on");
            }
	    logger.info("Output from device: "+st.toString());
                JSONObject json = new JSONObject(st.toString());
                for(Object keyObj : json.keySet()){
                    String key = (String) keyObj;
                    String value = (String) json.get(key).toString();
                    attributes.put(key, value);
                }
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                logger.severe(s);
            }
        } catch (IOException e) {
            logger.severe("Error polling Wemo Insight: " + e.toString());
        } catch (JSONException e) {
            logger.severe("Error parsing JSON respons from Wemo Insight: " + deviceId + ". " + e.getMessage());
	    e.printStackTrace();
        }

    }

    @Override
    public void saveCurrentState() {
        logger.info("[WeMoInsightMonitor] Saving current state");
        DeviceStatus wemo = new DeviceStatus(deviceId, attributes);
        sendToDeviceController(wemo);
        logger.info("[WeMoInsightMonitor] State saved: "+wemo.toString());
    }
}
