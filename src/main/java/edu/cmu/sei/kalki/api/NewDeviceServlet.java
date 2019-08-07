package edu.cmu.sei.kalki.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sei.ttg.kalki.models.*;
import org.eclipse.jetty.http.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;

public class NewDeviceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        System.out.println("Request received at /api/new-device/");
        Device device;
        JSONObject deviceData;

        // read body of request
        try {
            String bodyLine;
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader bodyReader = request.getReader();
            while((bodyLine = bodyReader.readLine()) != null){
                jsonBody.append(bodyLine);
            }
            deviceData = new JSONObject(jsonBody.toString());
        }
        catch (JSONException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            throw new ServletException("Error parsing body JSON of request: " + e.getMessage());
        }
        catch (IOException e) {
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            throw new ServletException("Error parsing body of request: " + e.getMessage());
        }

        // convert JSON to Device
        try {
            device = parseDevice(deviceData);
        }
        catch (JSONException e){
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            throw new ServletException("Error parsing device JSON " + e.getMessage());
        }

        // send device to DeviceMontitor to start a monitor
        response.setStatus(HttpStatus.OK_200);
    }

    protected Device parseDevice(JSONObject deviceData) throws JSONException {
        int id = deviceData.getInt("id");
        String name = deviceData.getString("name");
        String description = deviceData.getString("description");
        DeviceType deviceType = deviceData.optJSONObject("type")!=null ? parseDeviceType(deviceData.getJSONObject("type")):null;
        Group group = deviceData.optJSONObject("group")!=null ? parseGroup(deviceData.getJSONObject("group")):null;
        String ip = deviceData.getString("ip");
        int statusHistorySize = deviceData.getInt("statusHistorySize");
        int samplingRate = deviceData.getInt("samplingRate");
        DeviceSecurityState currentState = deviceData.optJSONObject("currentState")!=null ? parseSecurityState(deviceData.getJSONObject("currentState")):null;
        Alert lastAlert = deviceData.optJSONObject("lastAlert")!=null ? parseAlert(deviceData.getJSONObject("lastAlert")):null;
        Device device = new Device(name, description, deviceType, group, ip, statusHistorySize, samplingRate, currentState, lastAlert);
        device.setId(id);
        return device;
    }

    protected DeviceType parseDeviceType(JSONObject type) {
        int id = type.getInt("id");
        String name = type.getString("name");
        return new DeviceType(id, name);
    }

    protected Group parseGroup(JSONObject group) {
        int id = group.getInt("id");
        String name = group.getString("name");
        return new Group(id, name);
    }

    protected DeviceSecurityState parseSecurityState(JSONObject state) {
        int id = state.getInt("id");
        int deviceId = state.getInt("deviceId");
        int stateId = state.getInt("stateId");
        Timestamp timestamp = new Timestamp(state.getLong("timestamp"));
        String name = state.getString("name");
        return new DeviceSecurityState(id, deviceId, stateId, timestamp, name);
    }

    protected Alert parseAlert(JSONObject alert) {
        int id = alert.getInt("id");
        String name = alert.getString("name");
        Timestamp timestamp = new Timestamp(alert.getLong("timestamp"));
        String alerterId = alert.getString("alerterId");
        int deviceId = alert.getInt("deviceId");
        Integer deviceStatusId = alert.getInt("deviceStatusId");
        int alertTypeId = alert.getInt("alertTypeId");
        return new Alert(id, name, timestamp, alerterId, deviceId, deviceStatusId, alertTypeId);
    }
}
