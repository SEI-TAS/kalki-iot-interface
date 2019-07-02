package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class CurrentMwGreaterLow extends RulebookRule {

    public CurrentMwGreaterLow(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        float currentmw = Float.valueOf(status.getAttributes().get("currentmw"));

        if(currentmw > 1) {
            setAlertName("wemo-current-mw-greater-low");
            return true;
        }
        return false;
    }

}
