package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.api.*;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import static java.lang.Thread.sleep;

public class IotInterface {
    public static void main(String[] args) {
        Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");

        DeviceMonitor monitor = new DeviceMonitor();
        APIServerStartup.start(monitor);

//        while (true){
//
//        }
    }
}
