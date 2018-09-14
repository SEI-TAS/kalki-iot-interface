import kalkidb.database.Postgres;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResetDatabase {

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
        Postgres.resetDatabase();
        System.out.println("Successfully cleared tables in database.");
    }
}
