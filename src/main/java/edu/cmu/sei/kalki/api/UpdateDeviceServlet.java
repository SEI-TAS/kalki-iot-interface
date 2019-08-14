package edu.cmu.sei.kalki.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sei.kalki.DeviceMonitor;
import edu.cmu.sei.ttg.kalki.models.Device;
import org.eclipse.jetty.http.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class UpdateDeviceServlet extends ApiServlet {
    private Logger logger = Logger.getLogger("iot-interface");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.info("[UpdateDeviceServlet] Handling request.");

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

        logger.info("[UpdateDeviceServlet] Updating monitor for device: "+device.getId());
        response.setStatus(HttpStatus.OK_200);
        DeviceMonitor monitor = (DeviceMonitor) getServletContext().getAttribute("monitor");
        monitor.updateMonitor(device);
    }
}
