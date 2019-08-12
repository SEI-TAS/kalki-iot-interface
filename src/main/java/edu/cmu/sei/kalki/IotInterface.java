package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.api.*;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import static java.lang.Thread.sleep;

public class IotInterface {
    public static void main(String[] args) {
        Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");
        Postgres.resetDatabase();

        DeviceMonitor monitor = new DeviceMonitor();
//
//        Device device = new Device("Kalki", "description", Postgres.findDeviceType(3), "10.27.151.121", 1000, 1000);
//        device.insert();

//        monitor.startMonitor(device);
        APIServerStartup.start(monitor);

        while (true){

        }
    }
}
