package edu.cmu.sei.kalki.api;

import edu.cmu.sei.kalki.MonitorManager;
import edu.cmu.sei.kalki.utils.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.util.logging.Logger;

public class ApiServerStartup {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String API_URL = Config.data.get("iot_int_api_path");
    private static final int SERVER_PORT = 5050;

    /**
     * Starts a Jetty server, with handler for notifications
     */
    public static void start(MonitorManager monitor) throws Exception {
        try {
            Server httpServer = new Server(SERVER_PORT);
            ServletContextHandler handler = new ServletContextHandler(httpServer, API_URL);
            handler.addServlet(NewDeviceServlet.class, Config.data.get("new_device_path"));
            handler.addServlet(UpdateDeviceServlet.class, Config.data.get("update_device_path"));
            handler.addServlet(SendCommandServlet.class, Config.data.get("send_command_path"));
            handler.setAttribute("monitor", monitor);
            httpServer.start();

            logger.info("[ApiServerStartup] HTTP server started at " + httpServer.getURI().toString());
        } catch (Exception e) {
            logger.severe("[ApiServerStartup] Error starting IoT Interface API Server: "+e.getMessage());
            throw e;
        }
    }
}
