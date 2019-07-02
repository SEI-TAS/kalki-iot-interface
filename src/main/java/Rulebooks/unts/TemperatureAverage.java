package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class TemperatureAverage extends RulebookRule {

    public TemperatureAverage(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){

        setAlertName("unts-temperature-avg");
        return false;
    }

}
