package Database;

import kalkidb.models.*;
import kalkidb.database.Postgres;
import Monitors.IotMonitor;
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
        this.pgconn = (PGConnection)Postgres.dbConn;
        Postgres.executeCommand("LISTEN deviceinsert");
        // issue a dummy query to contact the backend
        // and receive any pending notifications.
        Postgres.executeCommand("SELECT 1");
    }

    public void run() {
        try {

            PGNotification notifications[] = pgconn.getNotifications();
            if (notifications != null) {
                for (PGNotification notification : notifications) {
                    int id = Integer.parseInt(notification.getParameter());
                    logger.info("New device with id " + notification.getParameter());
                    Postgres.findDevice(id).thenApplyAsync(device -> {
                        if (device == null){
                            logger.severe("Error: Null device on notified id.");
                        }
                        else{
                            IotMonitor monitor = IotMonitor.fromDevice(device);
                        }
                        return 1;
                    });
                }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}