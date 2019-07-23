package Rulebooks.wemo;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class TodayKwh extends RulebookRule {

    private final double kwhUpperBound = 0.220;

    public TodayKwh(){ }

    public boolean conditionIsTrue(){
        double todayKwH = Double.valueOf(status.getAttributes().get("today_kwh"));

        if (todayKwH > kwhUpperBound ){
            setAlertName("wemo-today-kwh");
            return true;
        }

        return false;
    }

}
