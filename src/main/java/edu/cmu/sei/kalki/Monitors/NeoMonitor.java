package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// SSH imports
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;

public class NeoMonitor extends PollingMonitor {

    private List<NeoSensor> sensors = new ArrayList<NeoSensor>();
    private String username;
    private String password;
    private String ip;
    private int deviceId;
    private DeviceStatus status;

    private Map<String, String> attributes = new HashMap<String, String>();

    public NeoMonitor(int deviceId, String ip, String username, String password, int samplingRate){
        this(deviceId, ip, samplingRate);
        this.username = username;
        this.password = password;
        this.isPollable = true;
    }

    public NeoMonitor(int deviceId, String ip, int samplingRate){
        logger.info("Starting Neo Monitor for deviceId: " + deviceId + ", ip: " + ip +
                ", and sampling rate: " + samplingRate + ".");
        this.ip = ip;
        this.deviceId = deviceId;
        this.username = "udooer";
        this.password = "udooer";
        this.pollInterval = samplingRate;
        this.isPollable = true;
        setSensors();
        start();
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
            Map<String, String> result = new HashMap<String, String>();
            if (responses.size() < 1){
                logger.severe("Missing response from Udoo Neo.");
            }
            else {
                String response = responses.get(0);
                responses.remove(0);
                String[] splits = response.split(",");
                result.put(name + "X", splits[0]);
                result.put(name + "Y", splits[1]);
                result.put(name + "Z", splits[2]);
            }
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

            String command = "";
            for(NeoSensor sensor: sensors){
                command += sensor.getCommand();
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, ip, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            Map<String, String> results = new HashMap<String, String>();

            // read the output from the command
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
            attributes = results;
            convertRawReadings();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e1){
            System.out.println("exception happend - here's what I know: ");
            e1.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

    }

    public void convertRawReadings(){
        //convert accelerometer readings to g's
        double accelCoefficient = 0.000244 / 4;
        convertThreeAxisReading("accelerometer", accelCoefficient);
        //convert gyroscope readings to degrees/second
        double gyroCoefficient = 0.0625;
        convertThreeAxisReading("gyroscope", gyroCoefficient);
        //convert magnetometer readings to micro Teslas
        double magCoefficient = 0.1;
        convertThreeAxisReading("magnetometer", magCoefficient);

        //convert temperature readings to celsius
        double tempCoefficient = 1/1000;
        convertTempReading("input", tempCoefficient);
        convertTempReading("max", tempCoefficient);
        convertTempReading("max_hyst", tempCoefficient);
    }

    private void convertThreeAxisReading(String sensor, double coefficient){
        double xReading = Double.valueOf(attributes.get(sensor+"X")) * coefficient;
        double yReading = Double.valueOf(attributes.get(sensor+"Y")) * coefficient;
        double zReading = Double.valueOf(attributes.get(sensor+"Z")) * coefficient;
        attributes.replace(sensor+"X", String.valueOf(xReading));
        attributes.replace(sensor+"Y", String.valueOf(yReading));
        attributes.replace(sensor+"Z", String.valueOf(zReading));
    }

    private void convertTempReading(String suffix, double coefficient) {
        double reading = Double.valueOf(attributes.get("temp_"+suffix)) * coefficient;
        attributes.replace("temp"+suffix, String.valueOf(reading));
    }

    @Override
    public void saveCurrentState() {
        logger.info("Saving current state");
        status = new DeviceStatus(deviceId, attributes);
        status.insert();
    }
}
