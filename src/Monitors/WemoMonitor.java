import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WemoMonitor extends IotMonitor {

    private String deviceName;

    public WemoMonitor(String deviceName){
        this.deviceName = deviceName;
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
            //String command = "wemo -v switch WeMo Insight status";
            //logger.info(command);
            //Process p = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                s = s.replace("Switch: " + deviceName, "");
                Boolean isOn = s.contains("on");
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

    }

    @Override
    public void connectToDevice() {

    }

    @Override
    public void saveCurrentState() {

    }
}