package edu.cmu.sei.kalki.iotinterface.plugins.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Monitor extends PollingMonitor {
    private static final String logId = "[PhleMonitor]";

    // TODO: This should be part of the device information.
    private String authCode = "newdeveloper"; //Default username works for most GET operations

    public Monitor(Device device, int samplingRate){
        super(device, true, samplingRate);
        start();
    }

    /**
     * Gets the state of the lights from the PHLE bridge
     * @param status The DeviceStatus to be inserted
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        JSONObject json = PHLEApi.getAllLights(device.getIp(), authCode);
        try {
            Set<String> keys = json.keySet();

            String key = keys.iterator().next(); //Assumes only one light is connected, does not verify
            status.addAttribute("lightId", key);
            JSONObject lightJson = json.getJSONObject(key);
            String name = lightJson.getString("name");
            JSONObject state = lightJson.getJSONObject("state");
            String brightness = Integer.toString(state.getInt("bri"));
            String hue = Integer.toString(state.getInt("hue"));
            String isOn = Boolean.toString(state.getBoolean("on"));
            status.addAttribute("hue", hue);
            status.addAttribute("isOn", isOn);
            status.addAttribute("brightness", brightness);
            status.addAttribute("name", name);
            logger.info(logId + " Successfully polled device.");
        } catch (JSONException err){
            logger.severe(logId + " Error: " + err.toString());
        }
    }

}
