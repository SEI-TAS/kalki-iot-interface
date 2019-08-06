//import Database.Postgres;
//import Models.Device;
//import Monitors.IotMonitor;
//import org.postgresql.PGNotification;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.sql.*;
//import java.util.Properties;
//import java.util.Random;
//
//import static java.lang.Thread.sleep;
//
//public class NotificationTest {
//
//    public static void main(String args[]) throws Exception {
//        Class.forName("org.postgresql.Driver");
//
//        // Create two distinct connections, one for the notifier
//        // and another for the listener to show the communication
//        // works across connections although this example would
//        // work fine with just one connection.
//
//        try{
//            //Read ip, port from config file
//
//            Postgres.initialize();
//            Postgres.resetDatabase();
//
//            DeviceListener listener = new DeviceListener();
//            Notifier notifier = new Notifier();
//            listener.start();
//            notifier.start();
//        }
//        catch(Exception e){
//
//        }
//    }
//
//}
//
//
//
//class Notifier extends Thread {
//
//    public void run() {
//        while (true) {
//            try {
//
//                Random rand = new Random();
//                int id = rand.nextInt(500000);
//                Device d = new Device(Integer.toString(id), "2", "WeMo Insight", "WeMo Insight", "wemo group",
//                        "", 20, 50);
//                d.insert();
//                sleep(2000);
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
//        }
//    }
//
//}