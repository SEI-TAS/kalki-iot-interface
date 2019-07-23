package Rulebooks.dlc;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

import java.util.Map;

@Rule()
public class MotionSense extends RulebookRule {

    public MotionSense(){ }

    public boolean conditionIsTrue(){
        boolean conditionIsTrue = false; // condition: motion_sense == true && PHLE.isOn == false

        boolean motionDetected = Boolean.parseBoolean(status.getAttributes().get("motion_detected"));

        if(motionDetected){
            for(Map.Entry<Device, DeviceStatus> entry: device.statusesOfSameGroup().entrySet()){
                Device d = entry.getKey();
                DeviceStatus s = entry.getValue();
                boolean isOn = Boolean.parseBoolean(s.getAttributes().get("isOn"));

                if(!isOn){ // a light is off
                    setAlertName("dlc-motion-sense");
                    conditionIsTrue = true;
                }
            }
        }



        return conditionIsTrue;
    }

}
