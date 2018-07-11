package main.java;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import main.java.Models.Device;
import main.java.Monitors.IotMonitor;
import main.java.Monitors.NeoMonitor;
import main.java.Monitors.WemoMonitor;
import main.java.Database.Postgres;

public class AddDevice {
    public static void main(String[] args) {

        Postgres.initialize();
//        Device d = new Device("2", "2", "myNeo", "Udoo Neo", "neo group",
//                "10.27.150.101", 20, 50);
//        d.insert();
        Device d = new Device("2", "2", "WeMo Insight", "WeMo Insight", "wemo group",
                "", 20, 50);
        d.insert();

    }
}
