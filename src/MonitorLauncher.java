import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MonitorLauncher {
    public static void main(String[] args) {

        Postgres.initialize();
        Postgres.resetDatabase();

        //IotMonitor monitor = new HueMonitor("localhost", 80);
        IotMonitor monitor = new WemoMonitor("WeMo Insight");
        monitor.start();
        //Device device = Postgres.findDevice("3c83ad5f-c041-47e9-9513-5d39322164b3");
        //System.out.println(device.group);

//        Postgres.resetDatabase();
//        Device device = new Device("hue", "127.0.0.1", "none", "");
//        device.addAttribute("brightness", "234");
//        device.addAttribute("isOn:", "true");
//        device.addAttribute("hue", "00");
//
//        device.insertOrUpdate();
//        List<Device> devices = Postgres.getAllDevices();
//
//        for(Device dev : devices){
//            System.out.println("Device info:");
//            if(dev.attributes.containsKey("hue")){
//                System.out.println(dev.attributes.get("hue"));
//
//            }
//            System.out.println(dev.id);
//        }
//        device.addAttribute("hue", "123");
//        device.insertOrUpdate();
//        System.out.println(Postgres.findDevice(device.id).attributes.get("brightness"));
//        devices = Postgres.getAllDevices();
//        System.out.println("Num devices:" +devices.size());
//
//        for(Device dev : devices){
//            if(dev.attributes.containsKey("hue")){
//                System.out.println(dev.attributes.get("hue"));
//
//            }
//                //System.out.println(dev.id);
//        }
    }
}
