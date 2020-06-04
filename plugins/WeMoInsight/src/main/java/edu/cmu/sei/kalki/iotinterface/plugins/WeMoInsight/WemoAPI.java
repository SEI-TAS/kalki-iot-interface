package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class WemoAPI
{
    private static final String LOG_ID = "[WemoScript] ";

    private static Logger logger = Logger.getLogger("iot-interface");

    private static final int WEMO_API_PORT = 7501;
    private static final String WEMO_BASE_PATH = "/plugins/wemo";
    private static final String WEMO_API_URL = "http://localhost:" + WEMO_API_PORT + WEMO_BASE_PATH;

    /**
     * Helper method to send a json object to the WemoAPI
     */
    public static String sendToApi(String command, String deviceIp){
        StringBuilder response = new StringBuilder();

        try {
            java.net.URL url = new URL(WEMO_API_URL + "/" + deviceIp + "/" + command);
            logger.info("Sending command to: " + url.toString());
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("POST");
            int responseCode = httpCon.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
                in.close();

                logger.info("Response: " + response.toString());
                return response.toString();
            }
            else
            {
                logger.severe("GET request was unsuccessful: " + responseCode);
                throw new RuntimeException("Problem sending request to server: " + responseCode);
            }
        } catch (IOException e) {
            logger.severe(LOG_ID + "Error sending command to Wemo API: " + command + "(" + deviceIp + ")");
            logger.severe(e.getMessage());
            throw new RuntimeException("Could not send command: " + e.getMessage());
        }
    }
}
