package main.java;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

//import src.main.edu.cmu.sei.ttg.kalkidb;//database.Postgres;
//import main.java.*;
//import main.java.database.*;
//import main.java.database.Postgres;
//import main.java.models.Device;
import kalkidb.models.*;
import kalkidb.database.Postgres;
import main.java.Monitors.IotMonitor;
import main.java.Monitors.NeoMonitor;
import main.java.Monitors.WemoMonitor;
import main.java.Database.DeviceListener;

public class MonitorLauncher {
    public static void main(String[] args) {

        try{
            Properties prop = new Properties();
            String fileName = "iotmon.config";
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            String port = prop.getProperty("POSTGRES_PORT");
            String ip = prop.getProperty("POSTGRES_IP");
            String dbUser = prop.getProperty("POSTGRES_USER");
            String dbName = prop.getProperty("POSTGRES_DBNAME");
            Postgres.initialize(ip, port, dbName, dbUser);
        }
        catch(IOException e){
            System.out.println("oops");
        }

        Postgres.resetDatabase();

        Device d1 = new Device("2", "2", "myNeo", "Udoo Neo", "neo group",
                "10.27.150.101", 20, 1000, "this is an udoo neo");
        Device d2 = new Device("2", "2", "WeMo Insight", "WeMo Insight", "wemo group",
                "", 20, 50, "/src");
        d1.insert();
        d1.insert();
        d1.insert();

//        //Listen for new devices inserted in the database to add more monitors.
        //DeviceListener.checkForDevices();
//
        //Start monitors for all existing devices in the database.
        List<Device> devices = Postgres.getAllDevices();
//
        for(Device device : devices){
            IotMonitor monitor = IotMonitor.fromDevice(device);
            monitor.start();
            System.out.println("device!");
        }

//        IotMonitor monitor = new WemoMonitor("1","WeMo Insight", 1000);
//        monitor.start();

    }
}
