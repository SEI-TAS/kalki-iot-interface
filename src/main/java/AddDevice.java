import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

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
            String dbPassword = prop.getProperty("POSTGRES_PASSWORD");
            Postgres.initialize(ip, port, dbName, dbUser, dbPassword);
        }
        catch(IOException e){

        }
//        DeviceT
//        Device d1 = new Device("Udoo Neo", "Desc", )
        Device d = new Device(2, "2", "WeMo Insight", 2, 1,
                "127.0.0.1", 20, 50);
        d.insert();

    }
}
