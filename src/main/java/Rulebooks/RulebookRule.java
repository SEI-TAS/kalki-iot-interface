package Rulebooks;

import kalkidb.database.Postgres;
import kalkidb.models.*;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

/**
 * @author camazzotta
 * @version 1.0
 * @created 11-Feb-2019 9:42:10 AM
 */
@Rule()
public abstract class RulebookRule {

	protected String alertName;
	@Given("device")
	protected Device device;
	@Given("status")
	protected DeviceStatus status;

	public RulebookRule(){}

	/**
	 * Rule-specific logic
	 */
	public abstract boolean conditionIsTrue();

	@Then
	public void then(){
		System.out.println("alerName in RulebookRule.java: "+alertName);

		Alert alert = new Alert(alertName, null, status.getId());
		alert.insert();
	}

	@When
	public boolean when(){
		return conditionIsTrue();
	}

	protected void setAlertName(String name){
		System.out.println("Setting alertName");
		alertName = name;
	}
}//end GenericRule
