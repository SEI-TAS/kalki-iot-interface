package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class CurrentMwSameGroup extends RulebookRule {

    public CurrentMwSameGroup(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        float currentmw = Float.valueOf(status.getAttributes().get("currentmw"));


        setAlertName("wemo-current-mw-same-group");
        return false;
    }

    private float groupAverage() {
        // get group statuses

        // calc average

        float sum = 0.0f;
//        int groupSize = list.length;
//        for(int i=0; i< len; i++){
//            DeviceStatus s = list.get(i);
//
//            if(s.getDeviceId() == device.getId()){ //dont include this device's status in the avg calculation
//                groupSize--;
//            } else {
//                float mw = Float.valueOf(s.getAttributes().get("currentmw"));
//                sum += mw;
//            }
//
//        }
//
//        return (sum / groupSize);
        return sum;
    }

}
