package edu.cmu.sei.kalki.api;

import edu.cmu.sei.kalki.DeviceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class APIServerStartup {
    private static final String API_URL = "/iot-interface-api";
    private static final int SERVER_PORT = 9090;

    /**
     * Starts a Jetty server, with handler for notifications
     */
    public static void start(DeviceMonitor monitor) {
        try {
            Server httpServer = new Server(SERVER_PORT);
            ServletContextHandler handler = new ServletContextHandler(httpServer, API_URL);
            handler.addServlet(NewDeviceServlet.class, "/new-device");
            handler.addServlet(UpdateDeviceServlet.class, "/update-device");
            handler.addServlet(SendCommandServlet.class, "/send-command");
            handler.setAttribute("monitor", monitor);
            httpServer.start();

            System.out.println("URI: "+httpServer.getURI().toString());
        } catch (Exception e) {
            System.out.println("Error starting IoT Interface API Server");
            e.printStackTrace();
        }
    }
}
