import Database.DeviceListener;
import kalkidb.database.Postgres;
import kalkidb.models.*;
import Monitors.IotMonitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class MonitorLauncher {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger( "myLogger" );

        try{
            Properties prop = new Properties();
            String fileName = "iotmon.config";
            InputStream is = new FileInputStream(fileName);
            prop.load(is);

            String port = prop.getProperty("POSTGRES_PORT");
            String ip = prop.getProperty("POSTGRES_IP");
            String dbUser = prop.getProperty("POSTGRES_USER");
            String dbName = prop.getProperty("POSTGRES_DB_NAME");
            String dbPassword = prop.getProperty("POSTGRES_PASSWORD");

            Postgres.initialize(ip, port, dbName, dbUser, dbPassword);
            logger.info("Succesfully initialized database.");
        }
        catch(IOException e){
            logger.severe("Error intializing database.");
            System.exit(-1);
        }

        //Postgres.setupDatabase();
        Postgres.resetDatabase();

        try{
            sleep(1000);
        }
        catch(Exception e){

        }

        try{
            sleep(1000);

        }
        catch(Exception e){

        }

//        Device d2 = new Device(1, "name", "WeMo Insight", 3, 1,
//                "127.0.0.2", 20, 1000);
//        d2.insert();

        //Listen for new devices inserted in the database to add more monitors.
        DeviceListener.checkForDevices();

        //Start monitors for all existing devices in the database.
        CompletionStage<List<Device>> deviceStage = Postgres.findAllDevices();
        deviceStage.thenApplyAsync(devices -> {
            for(Device device : devices){
                IotMonitor monitor = IotMonitor.fromDevice(device);
            }
            return 1;
        });

    }
}
