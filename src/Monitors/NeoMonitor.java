package Monitors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeoMonitor extends IotMonitor {

    private List<NeoSensor> sensors = new ArrayList<NeoSensor>();
    private String username;
    private String ip;

    public NeoMonitor(String ip, String username){
        this(ip);
        this.username = username;
    }

    public NeoMonitor(String ip){
        this.ip = ip;
        this.username = "udooer";
        setSensors();
    }

    public abstract class NeoSensor{
        public abstract String getCommand();
        public abstract Map<String, String> parseResponse(List<String> response);
    }

    public class NeoXYZSensor extends NeoSensor {

        private String name;

        public NeoXYZSensor(String name){
            this.name = name;
        }

        @Override
        public String getCommand(){
            return "cat /sensors/" + name + "/data;";
        }

        @Override
        public Map<String, String> parseResponse(List<String> responses) {
            String response = responses.get(0);
            responses.remove(0);
            String[] splits = response.split(",");
            Map<String, String> result = new HashMap<String, String>();
            result.put(name + "X", splits[0]);
            result.put(name + "Y", splits[1]);
            result.put(name + "Z", splits[2]);
            //System.out.println(result.toString());
            return result;
        }
    }

    public class TemperatureSensor extends NeoSensor {

        List<String> fields = new ArrayList<String>();

        public TemperatureSensor(){
            fields.add("max");
            fields.add("max_hyst");
            fields.add("input");
        }

        @Override
        public String getCommand(){
            String command = "";
            String base = "cat /sensors/temperature/temp1_";
            for(String field : fields){
                command += base + field + ";";
            }
            return command;
        }

        @Override
        public Map<String, String> parseResponse(List<String> responses) {
            Map<String, String> result = new HashMap<String, String>();
            for(String field: fields){
                String response = responses.get(0);
                responses.remove(0);
                result.put("temp" + field, response);
            }
            return result;
        }
    }

    public void setSensors(){
        sensors.add(new NeoXYZSensor("accelerometer"));
        sensors.add(new NeoXYZSensor("gyroscope"));
        sensors.add(new NeoXYZSensor("magnetometer"));
        sensors.add(new TemperatureSensor());
    }

    @Override
    public void pollDevice() {
        try {

            // run the command
            // using the Runtime exec method:

            String command = "";
            for(NeoSensor sensor: sensors){
                command += sensor.getCommand();
            }

            String[] args = new String[]{
                    "ssh",
                    username + "@" + ip,
                    command

            };
            Process p = Runtime.getRuntime().exec(args);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            Map<String, String> results = new HashMap<String, String>();
            // read the output from the command

            //System.out.println("Here is the standard output of the command:\n");
            List<String> lines = new ArrayList<String>();
            String s = stdInput.readLine();
            while(s != null){
                lines.add(s);
                s = stdInput.readLine();
            }

            for(NeoSensor sensor: sensors){
                results.putAll(sensor.parseResponse(lines));
            }

            System.out.println("Result is" + results.toString());

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
    public void saveCurrentState() {

    }
}