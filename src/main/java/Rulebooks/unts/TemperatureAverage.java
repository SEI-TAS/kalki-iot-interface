package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

import java.util.List;

@Rule()
public class TemperatureAverage extends RulebookRule {

    public TemperatureAverage(){ }

    public boolean conditionIsTrue(){
        double temp = Double.valueOf(status.getAttributes().get("temp_input"));
        List<DeviceStatus> lastNStatuses = Postgres.findNDeviceStatuses(device.getId(), 50);
        double avg = calculateAverage(lastNStatuses);

        if(temp > (avg + 2) || temp < (avg - 2)){
            setAlertName("unts-temperature-avg");
            return true;
        }
        return false;
    }

    private double calculateAverage(List<DeviceStatus> statuses) {
        double sum = 0;
        double num = statuses.size();
        for(DeviceStatus s:statuses){
            sum += Double.valueOf(s.getAttributes().get("temp_input"));
        }
        return sum / num;
    }

}
