package edu.cmu.sei.kalki.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.utils.HttpRequest;
import edu.cmu.sei.kalki.utils.PollingMonitor;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Monitor extends PollingMonitor {

    private String authCode = "newdeveloper"; //Default username works for most GET operations
    private String ip;

    public Monitor(int deviceId, String ip, int samplingRate){
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.ip = ip;
        logger.info("[PhleMonitor] Starting monitor.");
        start();
    }


    /**
     * Makes a get request to the PHLE REST API at the given path
     * @param path The endpoint of the api
     * @return String representation of the response from the API
     */
    public JSONObject issueCommand(String path){
        String targetURL = "http://" + ip + "/api/" + authCode + "/" + path;
        try{
            return HttpRequest.getRequest(targetURL);
        } catch (Exception e) {
            logger.severe("[PhleMonitor] Error getting a response from the bridge: "+ e);
            return new JSONObject("{\"error\": \"Error\"}");
        }
    }

    /**
     * Gets the state of the lights from the PHLE bridge
     * @param status The DeviceStatus to be inserted
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        JSONObject json = issueCommand("lights");
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
            logger.info("[PhleMonitor] Successfully polled device.");
        } catch (JSONException err){
            logger.severe("[PhleMonitor] Error: " + err.toString());
        }
    }

}
