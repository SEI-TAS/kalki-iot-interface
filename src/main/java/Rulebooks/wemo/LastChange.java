package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

import java.sql.Timestamp;
import java.util.List;

@Rule()
public class LastChange extends RulebookRule {

    public LastChange(){ }

    public boolean conditionIsTrue(){
        List<DeviceStatus> statuses = device.lastNSamples(2);

        if (statuses.size() > 1) {
            // convert timestamp escape format to long
            long ts1 = Timestamp.valueOf(statuses.get(0).getAttributes().get("lastchange")).getTime();
            long ts2 = Timestamp.valueOf(statuses.get(1).getAttributes().get("lastchange")).getTime();

            // if lastchange > 10 minutes
            if(Math.abs(ts1 - ts2) /60000 > 10){
                setAlertName("wemo-last-change");
                return true;
            }
        }
        return false;
    }

}



