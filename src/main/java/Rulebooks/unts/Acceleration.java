package Rulebooks.unts;

import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.Map;


@Rule()
public class Acceleration extends ThreeAxisRule {
	private final double accelXLowerBound = -0.01;
	private final double accelXUpperBound = 0.01;
	private final double accelYLowerBound = -0.0766;
	private final double accelYUpperBound = -0.0376;
	private final double accelZLowerBound = -1.126;
	private final double accelZUpperBound = 1.000;
	private final double accelModLimit = 1.12864;

	public Acceleration(){ }

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
		double accelX = Double.valueOf(status.getAttributes().get("accelerometerX"));
		double accelY = Double.valueOf(status.getAttributes().get("accelerometerY"));
		double accelZ = Double.valueOf(status.getAttributes().get("accelerometerZ"));

		if (	alertingAxis(accelX, accelXLowerBound, accelXUpperBound) ||
				alertingAxis(accelY, accelYLowerBound, accelYUpperBound) ||
				alertingAxis(accelZ, accelZLowerBound, accelZUpperBound) ||
				alertingModulus(accelX, accelY, accelZ, accelModLimit)) {
			setAlertName("unts-acceleration");
			return true;
		}

		return false;
	}
}
