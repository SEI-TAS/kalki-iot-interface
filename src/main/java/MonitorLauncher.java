package main.java;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

//import src.main.edu.cmu.sei.ttg.kalkidb;//database.Postgres;
//import main.java.*;
//import main.java.database.*;
import main.java.Database.Postgres;
import main.java.Models.Device;
import main.java.Monitors.IotMonitor;
import main.java.Monitors.NeoMonitor;
import main.java.Monitors.WemoMonitor;
import main.java.Database.DeviceListener;

public class MonitorLauncher {
    public static void main(String[] args) {

        Postgres.initialize();
        Postgres.resetDatabase();

//        Device d1 = new Device("2", "2", "myNeo", "Udoo Neo", "neo group",
//                "10.27.150.101", 20, 50);
//        Device d2 = new Device("2", "2", "WeMo Insight", "WeMo Insight", "wemo group",
//                "", 20, 50);
        //d.insert();

        //Listen for new devices inserted in the database to add more monitors.
        DeviceListener.checkForDevices();

        //Start monitors for all existing devices in the database.
        List<Device> devices = Postgres.getAllDevices();

        for(Device device : devices){
            IotMonitor monitor = device.toMonitor();
            monitor.start();
        }

//        IotMonitor monitor = new WemoMonitor("1","WeMo Insight", 1000);
//        monitor.start();

    }
}
