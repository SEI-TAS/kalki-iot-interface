package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class MagnetometerOnline extends RulebookRule {

    public MagnetometerOnline(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){

        setAlertName("unts-magnetometer-online");
        return false;
    }

}
