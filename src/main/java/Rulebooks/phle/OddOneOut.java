package Rulebooks.phle;

import java.util.Map;
import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class OddOneOut extends RulebookRule {

    public OddOneOut(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        boolean conditionIsTrue = true;

        // if this device is OFF
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn"))) {
            // get other devices of same type, check their status
            for(Map.Entry<Device, DeviceStatus> entry: device.statusesOfSameType().entrySet()){
                Device d = entry.getKey();
                DeviceStatus s = entry.getValue();
                boolean on = Boolean.parseBoolean(s.getAttributes().get("isOn"));

                if(device.getId() != d.getId() && !on){ // another light is also off
                    conditionIsTrue = false;
                }
            }
        }

        setAlertName("phle-odd-one-out");
        return conditionIsTrue;
    }
}
