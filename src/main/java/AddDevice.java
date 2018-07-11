package main.java;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kalkidb.models.Device;
import main.java.Monitors.IotMonitor;
import main.java.Monitors.NeoMonitor;
import main.java.Monitors.WemoMonitor;
import kalkidb.database.Postgres;

public class AddDevice {
    public static void main(String[] args) {

        try{
            Properties prop = new Properties();
            String fileName = "db.config";
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            String port = prop.getProperty("POSTGRES_PORT");
            String ip = prop.getProperty("POSTGRES_IP");
            String dbUser = prop.getProperty("POSTGRES_USER");
            String dbName = prop.getProperty("POSTGRES_DBNAME");
            Postgres.initialize(ip, port, dbName, dbUser);
        }
        catch(IOException e){

        }
//        Device d = new Device("2", "2", "myNeo", "Udoo Neo", "neo group",
//                "10.27.150.101", 20, 50);
//        d.insert();
        Device d = new Device("2", "2", "WeMo Insight", "WeMo Insight", "wemo group",
                "", 20, 50, "path");
        d.insert();

    }
}
