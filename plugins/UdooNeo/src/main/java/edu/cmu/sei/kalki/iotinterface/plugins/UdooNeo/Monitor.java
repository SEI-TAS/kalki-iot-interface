package edu.cmu.sei.kalki.iotinterface.plugins.UdooNeo;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

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

public class Monitor extends PollingMonitor {
    private static final String logId = "[UdooNeoMonitor]";
    private List<NeoSensor> sensors = new ArrayList<NeoSensor>();

    // TODO: This should be part of the device information.
    private String username = "udooer";
    private String password = "udooer";

    public Monitor(Device device, int samplingRate){
        super(device, true, samplingRate);
        setSensors();
        start();
    }

    /**
     * Abstract class for the sensors on an Udoo Neo
     */
    public abstract class NeoSensor{
        public abstract String getCommand();
        public abstract Map<String, String> parseResponse(List<String> response);
    }

    /**
     * Class to interface with an Udoo Sensor with X,Y,Z values
     */
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
                logger.severe(logId + " Missing response from Udoo Neo.");
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

    /**
     * Class to interface with an Udoo Temperature Sensor
     */
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

    /**
     * Sets the sensors that are available on the Udoo Neo
     */
    public void setSensors(){
        sensors.add(new NeoXYZSensor("accelerometer"));
        sensors.add(new NeoXYZSensor("gyroscope"));
        sensors.add(new NeoXYZSensor("magnetometer"));
        sensors.add(new TemperatureSensor());
    }

    /**
     * Connects to device, gets raw sensor readings, and converts to human-readable values
     * @param status The DeviceStatus to be sent to the DeviceControllerApi
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        try {

            Map<String, String> attributes = new HashMap<String, String>();

            String command = "";
            for(NeoSensor sensor: sensors){
                command += sensor.getCommand();
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, device.getIp(), 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            channel.connect();

            InputStream inStream = channel.getInputStream();
            logger.info(logId + " Sent commands to poll device");
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

            for(NeoSensor sensor: sensors){
                attributes.putAll(sensor.parseResponse(lines));
            }
            convertRawReadings(attributes);
            for (String key : attributes.keySet()){
                status.addAttribute(key, attributes.get(key));
            }
        } catch (JSchException e1){
            logger.severe(logId + " Exception happened - here's what I know: ");
            logger.severe(e1.getMessage());
        }
        catch (IOException e) {
            logger.severe(logId + " Exception happened - here's what I know: ");
            logger.severe(e.getMessage());
        }
        return;
    }

    /**
     * Helper method to convert the raw readings of sensors to human-readable values
     * @param attributes
     */
    public void convertRawReadings(Map<String,String> attributes){
        //convert accelerometer readings to g's
        double accelCoefficient = 0.000244 / 4;
        convertThreeAxisReading("accelerometer", accelCoefficient, attributes);
        //convert gyroscope readings to degrees/second
        double gyroCoefficient = 0.0625;
        convertThreeAxisReading("gyroscope", gyroCoefficient, attributes);
        //convert magnetometer readings to micro Teslas
        double magCoefficient = 0.1;
        convertThreeAxisReading("magnetometer", magCoefficient, attributes);

        //convert temperature readings to celsius
        double tempCoefficient = 1/1000;
        convertTempReading("input", tempCoefficient, attributes);
        convertTempReading("max", tempCoefficient, attributes);
        convertTempReading("max_hyst", tempCoefficient, attributes);
    }

    /**
     * Helper method to replace X,Y,Z values with converted value
     * @param sensor The sensor with X,Y,Z values
     * @param coefficient The conversion rate
     * @param attributes The device status attributes(sensor + X,Y, or Z)
     */
    private void convertThreeAxisReading(String sensor, double coefficient, Map<String,String> attributes){
        double xReading = Double.valueOf(attributes.get(sensor+"X")) * coefficient;
        double yReading = Double.valueOf(attributes.get(sensor+"Y")) * coefficient;
        double zReading = Double.valueOf(attributes.get(sensor+"Z")) * coefficient;
        attributes.replace(sensor+"X", String.valueOf(xReading));
        attributes.replace(sensor+"Y", String.valueOf(yReading));
        attributes.replace(sensor+"Z", String.valueOf(zReading));
    }

    /**
     * Helper method to replace temperature value with converted value
     * @param suffix The different temperature value
     * @param coefficient The conversion rate
     * @param attributes The device status attributes(sensor + suffix)
     */
    private void convertTempReading(String suffix, double coefficient, Map<String,String> attributes) {
        double reading = Double.valueOf(attributes.get("temp"+suffix)) * coefficient;
        attributes.replace("temp"+suffix, String.valueOf(reading));
    }

}
