package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class Magnetometer extends RulebookRule {

    public Magnetometer(){

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
        double magX = Double.valueOf(status.getAttributes().get("magnetometerX"));
        double magY = Double.valueOf(status.getAttributes().get("magnetometerY"));
        double magZ = Double.valueOf(status.getAttributes().get("magnetometerZ"));

        if(alertingMagnetometer(magX) || alertingMagnetometer(magY) || alertingMagnetometer(magZ) || alertingModulus(magX, magY, magZ)){
            setAlertName("unts-magnetometer");
            return true;
        }

        return false;
    }

    private boolean alertingMagnetometer(double mag) {
        if (mag > 5.0 || mag < -5.0) {
            return true;
        }
        return false;
    }

    private boolean alertingModulus(double magX, double magY, double magZ){
        double mod = Math.sqrt(magX*magY + magY*magY + magZ*magZ);
        if(mod > 5.0 || mod < -5.0) {
            return true;
        }
        return false;
    }

}
