package Rulebooks.unts;


import com.deliveredtechnologies.rulebook.annotation.*;

@Rule()
public class Gyro extends ThreeAxisRule {
    private final double gyroXLowerBound = -45;
    private final double gyroXUpperBound = 45;
    private final double gyroYLowerBound = -60;
    private final double gyroYUpperBound = 60;
    private final double gyroZLowerBound = -15;
    private final double gyroZUpperBound = 15;
    private final double gyroModLimit = 76.5;

    public Gyro(){ }

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

        if (    alertingAxis(gyroX, gyroXLowerBound, gyroXUpperBound) ||
                alertingAxis(gyroY, gyroYLowerBound, gyroYUpperBound) ||
                alertingAxis(gyroZ, gyroZLowerBound, gyroZUpperBound) ||
                alertingModulus(gyroX, gyroY, gyroZ, gyroModLimit)) {
            setAlertName("unts-gyro");
            return true;
        }

        return false;
    }

}
