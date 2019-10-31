package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
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


public class VizioMonitor extends PollingMonitor {

    private String deviceName;
    private Boolean isOn;
    private int deviceId;
    //private String authCode = "Zmwq86v1kj";
    //private String authCode = "Zvh5l45708";
    private String authCode = "Zfo377mxmc";
    private String ip;
    private int port = 7345;

    private Map<String, String> attributes;

    public VizioMonitor(int deviceId, String ip, int samplingRate){
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.ip = ip;
        logger.info("[VizioMonitor] Starting monitor.");
        start();
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public String issueCommand(String path){
        String targetURL = "https://" + ip + ":" + Integer.toString(port) + path;
        logger.info(targetURL);
        try{


            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial((chain, authType) -> true).build();

            SSLConnectionSocketFactory sslConnectionSocketFactory =
                    new SSLConnectionSocketFactory(sslContext, new String[]
                            {"SSLv2Hello", "SSLv3", "TLSv1","TLSv1.1", "TLSv1.2" }, null,
                            NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();

            HttpGet request = new HttpGet(targetURL);

            // add request headers
            request.addHeader("AUTH", authCode);
            request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
//            logger.info("[VizioMomitor] Executing request to " + targetURL);

            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                response.close();
                httpClient.close();
                return result;
            }
            return "Error";
        } catch (IOException e){
            logger.info("[VizioMonitor] Error: "+ e);
            return "Error";
        } catch (NoSuchAlgorithmException e) {
            logger.info("[VizioMonitor] Error: "+ e);
            return "Error";
        } catch (KeyStoreException e) {
            logger.info("[VizioMonitor] Error: "+ e);
            return "Error";
        } catch (KeyManagementException e) {
            logger.info("[VizioMonitor] Error: "+ e);
            return "Error";
        }
    }

    private String getCurrentInput(){
        String response = issueCommand("/menu_native/dynamic/tv_settings/devices/current_input");
        //logger.info("[VizioMonitor]  Getting current input from response " + response);
        try {
            String value = new JSONObject(response).getJSONArray("ITEMS").getJSONObject(0).getString("VALUE");
            return value;
        }catch (JSONException err){
            logger.severe("[VizioMonitor] Error: " + err.toString());
            return "Error";
        }
    }


    private String getInputListLength(){
        String response = issueCommand("/menu_native/dynamic/tv_settings/devices/name_input");
//        logger.info("[VizioMonitor]  Getting current input from response " + response);
        try {
            String value = Integer.toString(new JSONObject(response).getJSONArray("ITEMS").length());
            return value;
        }catch (JSONException err){
            logger.severe("[VizioMonitor]  Error: " + err.toString());
            return "Error";
        }
    }

    @Override
    public void pollDevice() {
        attributes = new HashMap<String, String>();
        attributes.put("input-list-size", getInputListLength());
        attributes.put("current-input", getCurrentInput());
    }

    @Override
    public void saveCurrentState() {
        logger.info("[VizioMonitor] Saving current state");
        DeviceStatus tvStatus = new DeviceStatus(deviceId, attributes);
        sendToDeviceController(tvStatus);
        logger.info("[VizioMonitor] State saved: "+ tvStatus.toString());
    }
}
