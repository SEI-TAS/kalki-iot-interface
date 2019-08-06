package edu.cmu.sei.kalki;

import edu.cmu.sei.kalki.Database.DeviceListener;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.*;
import edu.cmu.sei.kalki.Monitors.IotMonitor;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class MonitorLauncher {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger( "myLogger" );

        try{
            Properties prop = new Properties();
//            String fileName = "iot-interface.config";
//            InputStream is = new FileInputStream(fileName);
//            prop.load(is);

//            String port = prop.getProperty("POSTGRES_PORT");
//            String ip = prop.getProperty("POSTGRES_IP");
//            String dbUser = prop.getProperty("POSTGRES_USER");
//            String dbName = prop.getProperty("POSTGRES_DB_NAME");
//            String dbPassword = prop.getProperty("POSTGRES_PASSWORD");
//
//            Postgres.initialize(ip, port, dbName, dbUser, dbPassword);
            Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");

            logger.info("Succesfully initialized database.");
        }
        catch(Exception e){
            logger.severe("Error intializing database.");
            e.printStackTrace();
            System.exit(-1);
        }

        //Postgres.setupDatabase();
        Postgres.resetDatabase();

        try{ sleep(1000); } catch(Exception e){ }

        try{ sleep(1000); } catch(Exception e){ }

        System.out.println("\nINSERTING Udoo\n");
        DeviceType udooType = Postgres.findDeviceType(2);
        System.out.println(udooType.toString());
        Device udooTest = new Device("Udoo Test", "testing values", udooType, "10.27.151.101", 1000, 1000);
        udooTest.insert();

//        Postgres.deleteDevice(2);
//        Postgres.deleteDevice(3);

//        System.out.println("\nINSERTING Wemo\n");
//        DeviceType wemoType = Postgres.findDeviceType(3);
//        System.out.println(wemoType.toString());
//        Device wemoTest = new Device("Kalki", "testing values", wemoType, "10.27.151.121", 1000, 1000);
//        wemoTest.insert();

//        Listen for new devices inserted in the database to add more monitors.
        DeviceListener.checkForDevices();

//        Start monitors for all existing devices in the database
        List<Device> devices = Postgres.findAllDevices();
        for(Device device : devices){
            IotMonitor monitor = IotMonitor.fromDevice(device);
        }

    }
}
