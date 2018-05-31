import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class HueLight extends Device{
    
    private int brightness;
    private int hue;
    private boolean isOn;

    HueLight(String ip, int brightness, int hue, boolean isOn){
        super("Hue Light", ip);
        setAttributes(brightness, hue, isOn);

    }

    HueLight(String ip, int brightness, int hue, boolean isOn, String id){
        super("Hue Light", ip, "HueLight", id);
        setAttributes(brightness, hue, isOn);
    }

    HueLight(String ip, int brightness, int hue, boolean isOn, String id, String group){
        super("Hue Light", ip, group, id);
        setAttributes(brightness, hue, isOn);
    }

    public static HueLight fromDevice(Device device){
        Map<String, String> attr = device.attributes;
        int brightness = Integer.parseInt(attr.get("brightness"));
        int hue = Integer.parseInt(attr.get("hue"));
        boolean isOn = Boolean.parseBoolean(attr.get("isOn"));
        String hueId = attr.get("hueId");
        return new HueLight(device.ip, brightness, hue, isOn, device.id);
    }

    private void setAttributes(int brightness, int hue, boolean isOn){
        setBrightness(brightness);
        setHue(hue);
        setIsOn(isOn);
    }
    
    public void setBrightness(int brightness){
        this.brightness = brightness;
        addAttribute("brightness", Integer.toString(brightness));
    }

    public void setHue(int hue){
        this.hue = hue;
        addAttribute("hue", Integer.toString(hue));
    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
        addAttribute("isOn", Boolean.toString(isOn));
    }

    public int getBrightness(){
        return brightness;
    }

    public int getHue(){
        return hue;
    }

    public boolean getIsOn(){
        return isOn;
    }
}
