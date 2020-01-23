package edu.cmu.sei.kalki.iotinterface.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public static void putRequest(JSONObject body, String apiUrl) throws Exception {
        sendData(body, apiUrl, "PUT");
    }

    public static void postRequest(JSONObject body, String apiUrl) throws Exception {
        sendData(body, apiUrl, "POST");
    }

    private static void sendData(JSONObject body, String apiUrl, String method) throws IOException {
        HttpURLConnection con = makeConnection(apiUrl, true, method);
        appendBodyToRequest(con, body);
        con.getInputStream();
        con.disconnect();
    }

    public static JSONObject getRequest(String apiUrl) throws IOException {
        HttpURLConnection con = makeConnection(apiUrl, false, "GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line = "";
        while((line = in.readLine()) != null) {
            response.append(line);
        }

        con.disconnect();

        return new JSONObject(response.toString());
    }

    private static void appendBodyToRequest(HttpURLConnection con, JSONObject body) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(body.toString());
        out.close();
    }

    private static HttpURLConnection makeConnection(String endpoint, boolean output, String method) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(output);
        con.setRequestMethod(method);
        return con;
    }
}
