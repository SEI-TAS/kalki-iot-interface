package edu.cmu.sei.kalki.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.utils.Config;
import edu.cmu.sei.kalki.utils.HttpRequest;
import edu.cmu.sei.kalki.utils.PollingMonitor;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.util.Set;

//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHeaders;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;


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
        logger.info(targetURL);
        try{

//            int timeout = 5;
//            RequestConfig config = RequestConfig.custom()
//                    .setConnectTimeout(timeout * 1000)
//                    .setConnectionRequestTimeout(timeout * 1000)
//                    .setSocketTimeout(timeout * 1000).build();
//            CloseableHttpClient httpClient =
//                    HttpClientBuilder.create().setDefaultRequestConfig(config).build();
//            HttpGet request = new HttpGet(targetURL);
//
//
//            CloseableHttpResponse response = httpClient.execute(request);
//
//            HttpEntity entity = response.getEntity();
//            logger.info(entity.toString());
//            if (entity != null) {
//                logger.info("converting to string");
//                String result = EntityUtils.toString(entity);
//                logger.info("closing up");
//                response.close();
//                httpClient.close();
//                return result;
//            }

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
        logger.info("[PhleMonitor]  Getting current status from response " + json.toString());
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
        } catch (JSONException err){
            logger.severe("[PhleMonitor] Error: " + err.toString());
        }
    }

}
