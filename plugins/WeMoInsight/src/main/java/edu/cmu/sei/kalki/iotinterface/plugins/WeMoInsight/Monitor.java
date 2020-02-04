package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class Monitor extends PollingMonitor {
    private static final String logId = "[WemoMonitor]";

    public Monitor(int deviceId, String ip, int samplingRate) {
        super(deviceId, ip, true, samplingRate);
        logger.info(logId + " Starting monitor.");
        start();
    }

    /**
     * Executes the wemo python script to get a WemoInsight's status
     * @param status The DeviceStatus to be sent to the DeviceControllerApi
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        try {
            String output = WemoScript.executeScript("status", this.deviceIp);

            if(output != null) {
                JSONObject json = new JSONObject(output);
                for (Object keyObj : json.keySet())
                {
                    String key = (String) keyObj;
                    String value = json.get(key).toString();
                    status.addAttribute(key, value);
                }
            }
        } catch (JSONException e) {
            logger.severe(logId + " Error parsing JSON response from Wemo Insight: " + deviceId + ". " + e.getMessage());
	        e.printStackTrace();
        }

    }
}
