package edu.cmu.sei.kalki;
import edu.cmu.sei.kalki.api.*;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import static java.lang.Thread.sleep;

public class IotInterface {
    public static void main(String[] args) {
        Postgres.initialize("localhost", "5432", "kalkidb", "kalkiuser", "kalkipass");

        DeviceMonitor monitor = new DeviceMonitor();

        Device device = new Device("name", "description", Postgres.findDeviceType(2), "10.27.151.101", 1000, 10000);
        device.insert();

        monitor.startMonitor(device);

        try {
            sleep(50000);
        } catch (Exception e) { e.printStackTrace(); }


        device.setSamplingRate(20000);
        device.insertOrUpdate();

        monitor.updateMonitor(device);
//        APIServerStartup.start();
        while (true){

        }
    }
}
