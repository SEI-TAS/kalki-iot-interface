package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
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


    /**
     * UNTS DeviceStatus.attributes
     * {
     *     accelerometerX: "",
     *     accelerometerY: "",
     *     accelerometerZ: "",
     *     gyroscopeX: "",
     *     gyroscopeY: "",
     *     gyroscopeZ: "",
     *     magnetometerX: "",
     *     magnetometerY: "",
     *     magnetometerZ: "",
     *     tempmax: "",
     *     tempmax_hyst: "",
     *     tempinput: ""
     * }
     *
     *
     * @return
     */
    public boolean conditionIsTrue(){
        double temp = Double.valueOf(status.getAttributes().get("tempmax"));

        if (temp > 76.0 || temp < 66.0){
            setAlertName("unts-temperature");
            return true;
        }
        return false;
    }

}
