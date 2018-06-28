package Models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import Database.Postgres;

public class DeviceHistory {

    public String id;
    public String group;
    public Timestamp timestamp;
    public Map<String, String> attributes;
    public String deviceId;

    public DeviceHistory(String deviceId){
        this.attributes = new HashMap<String, String>();
        long millis = System.currentTimeMillis() % 1000;
        this.timestamp = new Timestamp(millis);
        this.deviceId = deviceId;
    }

    public DeviceHistory(String deviceId, Map<String, String> attributes) {
        this(deviceId);
        this.attributes = attributes;
    }

    public DeviceHistory(String deviceId, Map<String, String> attributes, Timestamp timestamp) {
        this(deviceId, attributes);
        this.timestamp = timestamp;
    }

    public DeviceHistory(String deviceId, Map<String, String> attributes, Timestamp timestamp, String id) {
        this(deviceId, attributes, timestamp);
        this.id = id;

    }

    public void addAttribute(String key, String value){
        attributes.put(key, value);
    }


    public void insert(){
        Postgres.insertDeviceHistory(this);
    }

    public void update(){
        Postgres.updateDeviceHistory(this);
    }

    public void insertOrUpdate(){
        Postgres.insertOrUpdateDeviceHistory(this);
    }
}
