package edu.cmu.sei.kalki.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sei.ttg.kalki.models.Device;
import org.eclipse.jetty.http.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateDeviceServlet extends APIServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        System.out.println("Request received at /api/update-device/");

        // read body of request
        JSONObject requestBody = parseRequestBody(request, response);

        // convert JSON to Device
        Device device;
        try {
            device = parseDevice(requestBody);
        }
        catch (JSONException e){
            throw new ServletException("Error parsing device JSON: " + e.getMessage());
        }

        // send device to DeviceMontitor to start a monitor
        System.out.println(device.toString());

        response.setStatus(HttpStatus.OK_200);
    }
}
