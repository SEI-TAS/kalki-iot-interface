package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class TimeOn extends RulebookRule {

    public TimeOn(){ }

    public boolean conditionIsTrue(){
        // today_on_time is in seconds
        int onTime = Integer.valueOf(status.getAttributes().get("today_on_time"));

        if (onTime > 32400){
            setAlertName("wemo-time-on");
            return true;
        }

        return false;
    }

}
