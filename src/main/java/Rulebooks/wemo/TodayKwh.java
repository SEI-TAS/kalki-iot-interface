package Rulebooks.wemo;

import kalkidb.database.Postgres;
import kalkidb.models.DeviceStatus;
import kalkidb.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class TodayKwh extends RulebookRule {

    public TodayKwh(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        double todayKwH = Double.valueOf(status.getAttributes().get("today_kwh"));

        // assuming a 100w (.1kw) lightbulb running for 10 hours
        if (todayKwH > 1 ){
            setAlertName("wemo-today-kwh");
            return true;
        }

        return false;
    }

}