package edu.cmu.sei.kalki.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sei.kalki.DeviceMonitor;
import edu.cmu.sei.kalki.commanders.DeviceCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import org.eclipse.jetty.http.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SendCommandServlet extends ApiServlet {
    private Logger logger = Logger.getLogger("iot-interface");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.info("[SendCommandServlet] Handling request");

        // read body of request
        JSONObject requestBody = parseRequestBody(request, response);

        // get Device from body
        Device device;
        try {
            device = parseDevice(requestBody.getJSONObject("device"));
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing device JSON: " + e.getMessage());
        }

        // get list of commands
        List<DeviceCommand> commandList;
        try {
            commandList = parseCommandList(requestBody.getJSONArray("command-list"));
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing command list JSON: " + e.getMessage());
        }
        response.setStatus(HttpStatus.OK_200);

        logger.info("[SendCommandServlet] Sending commands to device: "+device.toString());
        DeviceMonitor monitor = (DeviceMonitor) getServletContext().getAttribute("monitor");
        DeviceCommandSender.sendCommands(device, commandList, monitor.getApiUrl());
    }

    private List<DeviceCommand> parseCommandList(JSONArray commandList) {
        List<DeviceCommand> list = new ArrayList<>();
        for(int i=0;i<commandList.length();i++){
            JSONObject obj = commandList.getJSONObject(i);
            list.add(parseCommand(obj));
        }
        return list;
    }

    private DeviceCommand parseCommand(JSONObject command) {
        int id = command.getInt("id");
        String name = command.getString("name");
        int deviceTypeId = command.getInt("deviceTypeId");
        return new DeviceCommand(id, name, deviceTypeId);
    }

}
