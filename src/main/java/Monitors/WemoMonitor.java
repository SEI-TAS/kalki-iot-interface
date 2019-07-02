package Monitors;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;
import edu.cmu.sei.ttg.kalki.database.Postgres;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

// Rulebook imports
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

public class WemoMonitor extends PollingMonitor {

    private String deviceName;
    private Boolean isOn;
    private int deviceId;

    private Map<String, String> attributes;
    private DeviceStatus status;

    public WemoMonitor(int deviceId, String deviceName, int samplingRate){
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
        start();
    }

    @Override
    public void pollDevice() {
        String s = null;
        attributes = new HashMap<String, String>();
        try {

            // run the command
            // using the Runtime exec method:
            String[] args = new String[]{
                    "python",
                    "wemo.py",
                    deviceName
            };
            Process p = Runtime.getRuntime().exec(args);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                logger.info(s);
                JSONObject json = new JSONObject(s);
                for(Object keyObj : json.keySet()){
                    String key = (String) keyObj;
                    String value = (String) json.get(key).toString();
                    attributes.put(key, value);
                }

                s = s.replace("Switch: " + deviceName, "");
                isOn = s.contains("on");
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                logger.severe(s);
            }
        } catch (IOException e) {
            logger.severe("Error polling Wemo Insight: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void saveCurrentState() {
        DeviceStatus wemo = new DeviceStatus(deviceId, attributes);
        wemo.insert();
    }

    @Override
    public void runAlertRules() {
        Device device = Postgres.findDevice(deviceId);

        NameValueReferableMap facts = new FactMap();
        facts.setValue("device", device);
        facts.setValue("status", status);

        RuleBookRunner ruleBook = new RuleBookRunner("Rulebooks.wemo");
        ruleBook.run(facts);

    }
}
