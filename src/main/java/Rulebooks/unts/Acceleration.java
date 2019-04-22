package Rulebooks.unts;

import kalkidb.database.Postgres;
import kalkidb.models.DeviceStatus;
import kalkidb.models.Device;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

import java.util.Map;


@Rule()
public class Acceleration extends RulebookRule {

	public Acceleration(){

	}

	/**
	 *
	 * @exception Throwable Throwable
	 */
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
		double accelX = Double.valueOf(status.getAttributes().get("accelerometerX"));
		double accelY = Double.valueOf(status.getAttributes().get("accelerometerY"));
		double accelZ = Double.valueOf(status.getAttributes().get("accelerometerZ"));

		if (alertingAcceleration(accelX) || alertingAcceleration(accelY) || alertingAcceleration(accelZ) || alertingModulus(accelX, accelY, accelZ)) {
			setAlertName("unts-acceleration");
			return true;
		}


		return false;
	}

	private boolean alertingAcceleration(double accel){
		if( accel > 3.0 || accel < -3.0){
			return true;
		}
		return false;
	}

	private boolean alertingModulus(double accelX, double accelY, double accelZ){
		double mod = Math.sqrt(accelX*accelX + accelY*accelY + accelZ*accelZ);
		if ( mod > 3.0 || mod < -3.0){
			return true;
		}
		return false;
	}
}//end Acceleration