package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class PhilipsHueLightEmulatorMonitor extends PollingMonitor {


    private int deviceId;
    private String authCode = "newdeveloper"; //Default username works for most GET operations
    private String ip;

    private List<DeviceStatus> lights = new ArrayList<DeviceStatus>();
    Set<String> lightKeys = new HashSet<String>();

    public PhilipsHueLightEmulatorMonitor(int deviceId, String ip, int samplingRate, String url){
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.ip = ip;
        this.apiUrl = url;
        logger.info("[PhilipsHueLightEmulatorMonitor] Starting monitor.");
        start();
    }



    public String issueCommand(String path){
        String targetURL = "http://" + ip + "/api/" + authCode + "/" + path;
        logger.info(targetURL);
        try{

            int timeout = 5;
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000)
                    .setSocketTimeout(timeout * 1000).build();
            CloseableHttpClient httpClient =
                    HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            HttpGet request = new HttpGet(targetURL);


            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            logger.info(entity.toString());
            if (entity != null) {
                logger.info("converting to string");
                String result = EntityUtils.toString(entity);
                logger.info("closing up");
                response.close();
                httpClient.close();
                return result;
            }
            return "Error";
        } catch (IOException e) {
            logger.severe("[PhilipsHueLightEmulatorMonitor] Error: "+ e);
            return "Error";
        }
    }

    @Override
    public void pollDevice() {
        lights = new ArrayList<DeviceStatus>();
        String response = issueCommand("lights");
        logger.info("[PhilipsHueLightEmulatorMonitor]  Getting current status from response " + response);
        try {
            JSONObject json = new JSONObject(response);
            Iterator<String> keys = json.keys();
            while(keys.hasNext()) { // Each key is a different light, build set of keys to report empty statuses when device can't be reached
                String key = keys.next();
                if (!lightKeys.contains(key)) {
                    lightKeys.add(key);
                }
            }
            for (String key : lightKeys) {
                DeviceStatus light = new DeviceStatus(deviceId);
                light.addAttribute("lightId", key);
                JSONObject lightJson = json.getJSONObject(key);
                String name = lightJson.getString("name");
                JSONObject state = lightJson.getJSONObject("state");
                String brightness = Integer.toString(state.getInt("bri"));
                String hue = Integer.toString(state.getInt("hue"));
                String isOn = Boolean.toString(state.getBoolean("on"));
                light.addAttribute("hue", hue);
                light.addAttribute("isOn", isOn);
                light.addAttribute("brightness", brightness);
                light.addAttribute("name", name);
                lights.add(light);
            }
        } catch (JSONException err){
            logger.severe("[PhilipsHueLightEmulatorMonitor] Error: " + err.toString());
            for (String key : lightKeys){
                DeviceStatus light = new DeviceStatus(deviceId);
                lights.add(light);
            }
        }
    }


    @Override
    public void saveCurrentState() {
        for (DeviceStatus light : lights) {
            sendToDeviceController(light);
            logger.info("[PhilipsHueLightEmulatorMonitor] State saved: "+ light.toString());
        }
    }
}
