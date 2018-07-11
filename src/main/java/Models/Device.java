package main.java.Models;

import main.java.Database.Postgres;
import main.java.Monitors.HueMonitor;
import main.java.Monitors.IotMonitor;
import main.java.Monitors.NeoMonitor;
import main.java.Monitors.WemoMonitor;

public class Device {

    public String id;
    public String deviceId;
    public String name;
    public String type;
    public String groupId;
    public String ip;
    public int historySize;
    public int samplingRate;

    public Device(String id, String deviceId, String name, String type, String groupId, String ip,
                  int historySize, int samplingRate){
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.groupId = groupId;
        this.historySize = historySize;
        this.samplingRate = samplingRate;
        this.ip = ip;
    }

    public IotMonitor toMonitor() {
        if(type.equals("Udoo Neo")){
            return new NeoMonitor(deviceId, ip, samplingRate);
        }
        else if (type.equals("WeMo Insight")){
            return new WemoMonitor(deviceId, name, samplingRate);
        }
        else if(type.equals("Hue Light")){
            return new HueMonitor(ip, 80, deviceId, samplingRate);
        }
        return null;
    }

    public void insert(){
        Postgres.insertDevice(this);
    }

}
