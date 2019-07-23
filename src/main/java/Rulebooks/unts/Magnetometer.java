package Rulebooks.unts;

import com.deliveredtechnologies.rulebook.annotation.*;

@Rule()
public class Magnetometer extends ThreeAxisRule {
    private final double magXLowerBound = 80.0;
    private final double magXUpperBound = 90.0;
    private final double magYLowerBound = 80.0;
    private final double magYUpperBound = 90.0;
    private final double magZLowerBound = 90.0;
    private final double magZUpperBound = 110.0;
    private final double magModLimit = 168.226;

    private final double coefficient = 0.1; // converts raw readings to micro Teslas

    public Magnetometer(){ }

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

        if(     alertingAxis(magX, magXLowerBound, magXUpperBound) ||
                alertingAxis(magY, magYLowerBound, magYUpperBound) ||
                alertingAxis(magZ, magZLowerBound, magZUpperBound) ||
                alertingModulus(magX, magY, magZ, magModLimit)){
            setAlertName("unts-magnetometer");
            return true;
        }

        return false;
    }

}
