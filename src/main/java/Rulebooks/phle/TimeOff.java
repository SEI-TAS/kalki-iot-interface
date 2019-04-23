package Rulebooks.phle;

import kalkidb.database.Postgres;
import kalkidb.models.DeviceStatus;
import kalkidb.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class TimeOff extends RulebookRule {

    public TimeOff(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        // this status is OFF
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn"))) {

        }
            // query for last change of state
        // if last change > X (120 for now) minutes
        setAlertName("phle-time-off");
        return false;
    }

}