package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.IOException;
import java.io.InputStream;
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

public class UdooNeoMonitor extends PollingMonitor {

    private List<NeoSensor> sensors = new ArrayList<NeoSensor>();
    private String username;
    private String password;
    private String ip;
    private int deviceId;
    private DeviceStatus status;

    private Map<String, String> attributes = new HashMap<String, String>();

    public UdooNeoMonitor(int deviceId, String ip, String username, String password, int samplingRate, String url){
        this(deviceId, ip, samplingRate, url);
        this.username = username;
        this.password = password;
        this.isPollable = true;
    }

    public UdooNeoMonitor(int deviceId, String ip, int samplingRate, String url){
        super();
        logger.info("[UdooNeoMonitor] Starting Neo Monitor for device: " + deviceId);
        this.ip = ip;
        this.deviceId = deviceId;
        this.username = "udooer";
        this.password = "udooer";
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.apiUrl = url;
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
                logger.severe("[UdooNeoMonitor] Missing response from Udoo Neo.");
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

            channel.connect();

            InputStream inStream = channel.getInputStream();
            logger.info("[UdooNeoMonitor] Sent commands to device");
            List<String> lines = new ArrayList<String>();

            int c = inStream.read();
            StringBuilder string = new StringBuilder();
            while (c > -1) {
                if (inStream.available() == 0) { // read the newline char
                    lines.add(string.toString());
                    string = new StringBuilder();
                } else {
                    string.append((char)c);
                }
                c = inStream.read();
            }

            channel.disconnect();
            session.disconnect();

            Map<String, String> results = new HashMap<String, String>();
            for(NeoSensor sensor: sensors){
                results.putAll(sensor.parseResponse(lines));
            }

            attributes = results;
            convertRawReadings();
        } catch (JSchException e1){
            logger.severe("[UdooNeoMonitor] Exception happened - here's what I know: ");
            logger.severe(e1.getMessage());
        }
        catch (IOException e) {
            logger.severe("[UdooNeoMonitor] Exception happened - here's what I know: ");
            logger.severe(e.getMessage());
        }
        return;
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
        double reading = Double.valueOf(attributes.get("temp"+suffix)) * coefficient;
        attributes.replace("temp"+suffix, String.valueOf(reading));
    }

    @Override
    public void saveCurrentState() {
        logger.info("[UdooNeoMonitor] Saving current state");
        status = new DeviceStatus(deviceId, attributes);
        sendToDeviceController(status);
    }
}