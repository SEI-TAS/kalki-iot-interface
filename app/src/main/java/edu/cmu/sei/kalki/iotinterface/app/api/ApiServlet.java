package edu.cmu.sei.kalki.iotinterface.app.api;

import edu.cmu.sei.kalki.db.models.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Base class for ApiServlets to handle requests from the DeviceController
 */
public class ApiServlet extends HttpServlet {

    /**
     * Method to extract body of an HTTP request and convert it to a JSON object
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    protected JSONObject parseRequestBody(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject requestBody;
        try {
            String bodyLine;
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader bodyReader = request.getReader();
            while((bodyLine = bodyReader.readLine()) != null) {
                jsonBody.append(bodyLine);
            }
            requestBody = new JSONObject(jsonBody.toString());
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing body JSON of request: " + e.getMessage());
        }
        catch (IOException e) {
            throw new ServletException("Error parsing body of request: " + e.getMessage());
        }

        return requestBody;
    }

    /**
     * Method to convert a JSONObject to a Device
     * @param deviceData
     * @return
     * @throws JSONException
     */
    protected Device parseDevice(JSONObject deviceData) throws JSONException {
        int id = deviceData.getInt("id");
        String name = deviceData.getString("name");
        String description = deviceData.getString("description");
        DeviceType deviceType = deviceData.optJSONObject("type")!=null ? parseDeviceType(deviceData.getJSONObject("type")):null;
        Group group = deviceData.optJSONObject("group")!=null ? parseGroup(deviceData.getJSONObject("group")):null;
        String ip = deviceData.getString("ip");
        int statusHistorySize = deviceData.getInt("statusHistorySize");
        int samplingRate = deviceData.getInt("samplingRate");
        int defaultSamplingRate = deviceData.getInt("defaultSamplingRate");
        DeviceSecurityState currentState = deviceData.optJSONObject("currentState")!=null ? parseSecurityState(deviceData.getJSONObject("currentState")):null;
        Alert lastAlert = deviceData.optJSONObject("lastAlert")!=null ? parseAlert(deviceData.getJSONObject("lastAlert")):null;
        DataNode datNode = deviceData.optJSONObject("dataNode")!=null ? parseDataNode(deviceData.getJSONObject("dataNode")):null;
        Device device = new Device(name, description, deviceType, group, ip, statusHistorySize, samplingRate, defaultSamplingRate,currentState, lastAlert, datNode);
        device.setId(id);
        return device;
    }

    /**
     * Method to convert a JSONObject to a DeviceType
     * @param type
     * @return
     */
    protected DeviceType parseDeviceType(JSONObject type) {
        int id = type.getInt("id");
        String name = type.getString("name");
        return new DeviceType(id, name);
    }

    /**
     * Method to convert a JSONObject to a Group
     * @param group
     * @return
     */
    protected Group parseGroup(JSONObject group) {
        int id = group.getInt("id");
        String name = group.getString("name");
        return new Group(id, name);
    }

    /**
     * Method to convert a JSONObject to a DeviceSecurityState
     * @param state
     * @return
     */
    protected DeviceSecurityState parseSecurityState(JSONObject state) {
        int id = state.getInt("id");
        int deviceId = state.getInt("deviceId");
        int stateId = state.getInt("stateId");
        Timestamp timestamp = new Timestamp(state.getLong("timestamp"));
        String name = state.getString("name");
        return new DeviceSecurityState(id, deviceId, stateId, timestamp, name);
    }

    /**
     * Method to convert a JSONObject to an Alert
     * @param alert
     * @return
     */
    protected Alert parseAlert(JSONObject alert) {
        int id = alert.getInt("id");
        String name = alert.getString("name");
        Timestamp timestamp = new Timestamp(alert.getLong("timestamp"));
        String alerterId = alert.getString("alerterId");
        int deviceId = alert.getInt("deviceId");
        Integer deviceStatusId = alert.getInt("deviceStatusId");
        int alertTypeId = alert.getInt("alertTypeId");
        String info = alert.getString("info");
        return new Alert(id, name, timestamp, alerterId, deviceId, deviceStatusId, alertTypeId, info);
    }

    /**
     * Method to convert a JSONObject to an DataNode
     * @param dataNode
     * @return
     */
    protected DataNode parseDataNode(JSONObject dataNode) {
        int id = dataNode.getInt("id");
        String name = dataNode.getString("name");
        String ipAddress = dataNode.getString("ipAddress");
        return new DataNode(id, name, ipAddress);
    }
}
