package Rulebooks.unts;

import edu.cmu.sei.ttg.kalki.database.Postgres;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class Gyro extends RulebookRule {

    public Gyro(){

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
        double gyroX = Double.valueOf(status.getAttributes().get("gyroscopeX"));
        double gyroY = Double.valueOf(status.getAttributes().get("gyroscopeY"));
        double gyroZ = Double.valueOf(status.getAttributes().get("gyroscopeZ"));

        if (alertingGyro(gyroX) || alertingGyro(gyroY) || alertingGyro(gyroZ) || alertingModulus(gyroX, gyroY, gyroZ)) {
            setAlertName("unts-gyro");
            return true;
        }

        return false;
    }

    private boolean alertingGyro(double gyro){
        if( gyro > 3.0 || gyro < -3.0){
            return true;
        }
        return false;
    }

    private boolean alertingModulus(double gyroX, double gyroY, double gyroZ){
        double mod = Math.sqrt(gyroX*gyroX + gyroY*gyroY + gyroZ*gyroZ);
        if ( mod > 3.0 || mod < -3.0){
            return true;
        }
        return false;
    }

}
