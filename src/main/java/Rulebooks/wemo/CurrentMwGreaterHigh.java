package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class CurrentMwGreaterHigh extends RulebookRule {

    public CurrentMwGreaterHigh(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        float currentmw = Float.valueOf(status.getAttributes().get("currentmw"));

        if( currentmw > 2 ){
            setAlertName("wemo-current-mw-greater-high");
            return true;
        }

        return false;
    }

}
