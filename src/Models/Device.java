import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Device {

    public String ip = "123";
    public String deviceName;
    public String id;
    public String group;
    public Timestamp timestamp;
    public Map<String, String> attributes;

    public Device(String deviceName, String ip){
        this.deviceName = deviceName;
        this.ip = ip;
        this.id = UUID.randomUUID().toString();
        this.attributes = new HashMap<String,String>();
        this.group = "none";
        long millis = System.currentTimeMillis() % 1000;
        timestamp = new Timestamp(millis);
    }

    public Device(String deviceName, String ip, String group){
        this(deviceName, ip);
        this.group = group;

    }

    public Device(String deviceName, String ip, String group, String id){
        this(deviceName, ip, group);
        this.id = id;
    }

    public Device(String deviceName, String ip, String group, String id, Map<String, String> attributes,
                  Timestamp timestamp){
        this(deviceName, ip, id, group);
        this.attributes = attributes;
        this.timestamp = timestamp;
    }

    public void addAttribute(String key, String value){
        attributes.put(key, value);
    }

    public void insert(){
        Postgres.insertDevice(this);
    }

    public void update(){
        Postgres.updateDevice(this);
    }

    public void insertOrUpdate(){
        Postgres.insertOrUpdateDevice(this);
    }
}
