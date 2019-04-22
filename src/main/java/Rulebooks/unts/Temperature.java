package Rulebooks.unts;

import kalkidb.database.Postgres;
import kalkidb.models.DeviceStatus;
import kalkidb.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class Temperature extends RulebookRule {

    public Temperature(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){

        setAlertName("unts-temperature");
        return false;
    }

}