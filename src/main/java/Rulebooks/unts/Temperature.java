package Rulebooks.unts;

import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class Temperature extends RulebookRule {
    private final double tempLowerBound = 20.0;
    private final double tempUpperBound = 23.0;

    public Temperature(){ }

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
        double temp = Double.valueOf(status.getAttributes().get("temp_input"));

        if (temp < tempLowerBound || temp > tempUpperBound){
            setAlertName("unts-temperature");
            return true;
        }
        return false;
    }

}
