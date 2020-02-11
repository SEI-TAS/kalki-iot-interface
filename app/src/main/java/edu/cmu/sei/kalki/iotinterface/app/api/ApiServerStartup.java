package edu.cmu.sei.kalki.iotinterface.app.api;

import edu.cmu.sei.kalki.iotinterface.app.MonitorManager;
import edu.cmu.sei.kalki.iotinterface.common.utils.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.util.logging.Logger;

public class ApiServerStartup {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String API_URL = "/iot-interface-api";
    private static final int SERVER_PORT = Integer.parseInt(Config.data.get("iot_interface_api_port"));

    /**
     * Starts a Jetty server, with handler for notifications
     */
    public static void start(MonitorManager monitor) throws Exception {
        try {
            Server httpServer = new Server(SERVER_PORT);
            ServletContextHandler handler = new ServletContextHandler(httpServer, API_URL);
            handler.addServlet(NewDeviceServlet.class, "/new-device");
            handler.addServlet(UpdateDeviceServlet.class, "/update-device");
            handler.addServlet(SendCommandServlet.class, "/send-command");
            handler.setAttribute("monitor", monitor);
            httpServer.start();

            logger.info("[ApiServerStartup] HTTP server started at " + httpServer.getURI().toString());
        } catch (Exception e) {
            logger.severe("[ApiServerStartup] Error starting IoT Interface API Server: "+e.getMessage());
            throw e;
        }
    }
}
