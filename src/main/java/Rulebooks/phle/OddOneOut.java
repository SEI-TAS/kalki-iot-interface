//package Rulebooks.unts;
//import Rulebooks.GenericRule;
//import kalkidb.database.Postgres;
//import kalkidb.models.*;
//
//import com.deliveredtechnologies.rulebook.RuleState;
//import com.deliveredtechnologies.rulebook.annotation.*;
///**
// * @author camazzotta
// * @version 1.0
// * @created 11-Feb-2019 9:55:06 AM
// */
//@Rule()
//public class Acceleration extends GenericRule {
////	@Given("device")
////	protected Device device;
////	@Given("status")
////	protected DeviceStatus status;
//
//	private String alertName = "unts-acceleration-alert";
//
//	public Acceleration(){}
//
//	@Override
//	public boolean conditionIsTrue(){
//		float acceleration = Float.valueOf(this.status.getAttributes().get("acceleration"));
////		System.out.println("Acceleration: "+acceleration);
//		System.out.println("alerName in Acceleration.java: "+alertName);
//		if (acceleration > 3.0) {
//			System.out.println("SUPER SUS");
//			setAlertName("unts-acceleration-alert");
//			return true;
//		}
//
//		return false;
//	}
//}//end Acceleration
package Rulebooks.phle;

import java.util.Map;
import kalkidb.database.Postgres;
import kalkidb.models.DeviceStatus;
import kalkidb.models.Device;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;
import Rulebooks.RulebookRule;

@Rule()
public class OddOneOut extends RulebookRule {

    public OddOneOut(){

    }

    public void finalize()
            throws Throwable{
    }

    public boolean conditionIsTrue(){
        boolean conditionIsTrue = true;

        // if this device is OFF
        if(!Boolean.parseBoolean(status.getAttributes().get("isOn"))) {
            // get other devices of same type, check their status
            for(Map.Entry<Device, DeviceStatus> entry: device.statusesOfSameType().entrySet()){
                Device d = entry.getKey();
                DeviceStatus s = entry.getValue();
                boolean on = Boolean.parseBoolean(s.getAttributes().get("isOn"));

                if(device.getId() != d.getId() && !on){ // another light is also off
                    conditionIsTrue = false;
                }
            }
        }

        setAlertName("phle-odd-one-out");
        return conditionIsTrue;
    }
}