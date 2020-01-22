package edu.cmu.sei.kalki.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cmu.sei.kalki.MonitorManager;
import edu.cmu.sei.ttg.kalki.models.*;
import org.eclipse.jetty.http.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class NewDeviceServlet extends ApiServlet {
    private Logger logger = Logger.getLogger("iot-interface");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.info("[NewDeviceServlet] Handling request.");

        // read body of request
        JSONObject requestBody = parseRequestBody(request, response);

        // convert JSON to Device
        Device device;
        try {
            device = parseDevice(requestBody);
        }
        catch (JSONException e){
            throw new ServletException("Error parsing device JSON " + e.getMessage());
        }

        logger.info("[NewDeviceServlet] Calling startMonitor for device: "+device.toString());
        response.setStatus(HttpStatus.OK_200);

        MonitorManager monitor = (MonitorManager) getServletContext().getAttribute("monitor");
        monitor.startMonitor(device);
    }


}
