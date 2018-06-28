package Models;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class WemoInsight extends Device{

    private boolean isOn;
    private String wemoName;

    public WemoInsight(String ip, String wemoName, boolean isOn){
        super("Wemo Insight", ip);
        setAttributes(wemoName, isOn);

    }

    public WemoInsight(String ip, String wemoName, boolean isOn, String id){
        super("WeMo Insight", ip, "Wemo Insight", id);
        setAttributes(wemoName, isOn);
    }

    public WemoInsight(String ip, String wemoName, String id, String group){
        super("Wemo Insight", ip, group, id);
        setAttributes(wemoName, isOn);
    }

    public static WemoInsight fromDevice(Device device){
        Map<String, String> attr = device.attributes;
        String wemoName = attr.get("wemoName");
        boolean isOn = Boolean.parseBoolean(attr.get("isOn"));
        return new WemoInsight(device.ip, wemoName, isOn, device.id);
    }

    private void setAttributes(String wemoName, boolean isOn){
        setIsOn(isOn);
        setWemoName(wemoName);
    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
        addAttribute("isOn", Boolean.toString(isOn));
    }

    public void setWemoName(String wemoName){
        this.wemoName = wemoName;
        addAttribute("wemoName", wemoName);
    }

    public boolean getIsOn(){
        return isOn;
    }
}
