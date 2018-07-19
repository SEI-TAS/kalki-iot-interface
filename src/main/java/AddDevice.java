import kalkidb.database.Postgres;
import kalkidb.models.Device;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
