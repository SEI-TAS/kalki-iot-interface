package main.java.Database;

import main.java.Database.Postgres;
import main.java.Models.Device;
import main.java.Monitors.IotMonitor;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class DeviceListener extends TimerTask {

    public static void checkForDevices(){
        int pollInterval = 1000;
        Timer timer = new Timer();
        timer.schedule(new DeviceListener(), pollInterval, pollInterval);
    }

    private static Logger logger = Logger.getLogger("myLogger");

    private PGConnection pgconn;

    private DeviceListener() {
        this.pgconn = (PGConnection)Postgres.db;
        Postgres.executeCommand("LISTEN deviceinsert");
    }

    public void run() {
        try {
            // issue a dummy query to contact the backend
            // and receive any pending notifications.
            Postgres.executeCommand("SELECT 1");

            PGNotification notifications[] = pgconn.getNotifications();
            if (notifications != null) {
                for (PGNotification notification : notifications) {
                    String id = notification.getParameter();
                    logger.info("New device with id " + notification.getParameter());
                    Device device = Postgres.findDevice(id);
                    if (device == null){
                        logger.severe("Error: Null device on notified id.");
                    }
                    else{
                        IotMonitor monitor = device.toMonitor();
                        monitor.start();
                    }
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}