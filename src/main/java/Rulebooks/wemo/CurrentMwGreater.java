package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class CurrentMwGreater extends RulebookRule {
    private final double currentMwLowThreshold = 17040;
    private final double currentMwHightThreshold = 17050;

    public CurrentMwGreater() { }


    public boolean conditionIsTrue(){
        double currentmw = Double.valueOf(status.getAttributes().get("currentpower"));

        if(currentmw > currentMwHightThreshold) {
            setAlertName("wemo-current-mw-greater-high");
            return true;
        }
        else if(currentmw > currentMwLowThreshold){
            setAlertName("wemo-current-mw-greater-low");
            return true;
        }
        else {
            return false;
        }

    }

}
