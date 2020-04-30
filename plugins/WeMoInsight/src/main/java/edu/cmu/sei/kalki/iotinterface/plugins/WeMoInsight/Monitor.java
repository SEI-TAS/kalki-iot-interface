package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class Monitor extends PollingMonitor {
    private static final String logId = "[WemoMonitor]";

    public Monitor(Device device) {
        super(device);
    }

    /**
     * Executes the wemo python script to get a WemoInsight's status
     * @param status The DeviceStatus to be sent to the DeviceControllerApi
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        try {
            String output = WemoAPI.sendToApi("status", device.getIp());

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
            logger.severe(logId + " Error parsing JSON response from Wemo Insight: " + device.getName() + ". " + e.getMessage());
	        e.printStackTrace();
        }

    }
}
