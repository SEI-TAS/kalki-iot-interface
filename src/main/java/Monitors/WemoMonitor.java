package Monitors;

import kalkidb.models.DeviceHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WemoMonitor extends IotMonitor {

    private String deviceName;
    private Boolean isOn;
    private String deviceId;

    public WemoMonitor(String deviceId, String deviceName, int samplingRate){
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.pollInterval = samplingRate;
    }

    @Override
    public void pollDevice() {
        String s = null;
        try {

            // run the command
            // using the Runtime exec method:
            String[] args = new String[]{
                    "wemo",
                    "-v",
                    "switch",
                    deviceName,
                    "status"
            };
            Process p = Runtime.getRuntime().exec(args);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
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
        DeviceHistory wemo = new DeviceHistory(deviceId);
        wemo.addAttribute("isOn", isOn.toString());
        wemo.insertOrUpdate();
    }
}