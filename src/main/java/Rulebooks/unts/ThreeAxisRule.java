package Rulebooks.unts;

import Rulebooks.RulebookRule;
import com.deliveredtechnologies.rulebook.annotation.*;

@Rule()
public abstract class ThreeAxisRule extends RulebookRule {

    public ThreeAxisRule () { }

    protected boolean alertingAxis(double axis, double lowerBound, double upperBound) {
        if( axis < lowerBound || axis > upperBound){
            return true;
        }
        return false;
    }

    protected boolean alertingModulus(double xAxis, double yAxis, double zAxis, double limit) {
        double modulus = Math.sqrt(xAxis*xAxis + yAxis*yAxis + zAxis*zAxis);
        if(modulus < (-1 * limit) || modulus > limit) {
            return true;
        }
        return false;
    }
}
