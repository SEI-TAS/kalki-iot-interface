/*
 * Kalki - A Software-Defined IoT Security Platform
 * Copyright 2020 Carnegie Mellon University.
 * NO WARRANTY. THIS CARNEGIE MELLON UNIVERSITY AND SOFTWARE ENGINEERING INSTITUTE MATERIAL IS FURNISHED ON AN "AS-IS" BASIS. CARNEGIE MELLON UNIVERSITY MAKES NO WARRANTIES OF ANY KIND, EITHER EXPRESSED OR IMPLIED, AS TO ANY MATTER INCLUDING, BUT NOT LIMITED TO, WARRANTY OF FITNESS FOR PURPOSE OR MERCHANTABILITY, EXCLUSIVITY, OR RESULTS OBTAINED FROM USE OF THE MATERIAL. CARNEGIE MELLON UNIVERSITY DOES NOT MAKE ANY WARRANTY OF ANY KIND WITH RESPECT TO FREEDOM FROM PATENT, TRADEMARK, OR COPYRIGHT INFRINGEMENT.
 * Released under a MIT (SEI)-style license, please see license.txt or contact permission@sei.cmu.edu for full terms.
 * [DISTRIBUTION STATEMENT A] This material has been approved for public release and unlimited distribution.  Please see Copyright notice for non-US Government use and distribution.
 * This Software includes and/or makes use of the following Third-Party Software subject to its own license:
 * 1. Google Guava (https://github.com/google/guava) Copyright 2007 The Guava Authors.
 * 2. JSON.simple (https://code.google.com/archive/p/json-simple/) Copyright 2006-2009 Yidong Fang, Chris Nokleberg.
 * 3. JUnit (https://junit.org/junit5/docs/5.0.1/api/overview-summary.html) Copyright 2020 The JUnit Team.
 * 4. Play Framework (https://www.playframework.com/) Copyright 2020 Lightbend Inc..
 * 5. PostgreSQL (https://opensource.org/licenses/postgresql) Copyright 1996-2020 The PostgreSQL Global Development Group.
 * 6. Jackson (https://github.com/FasterXML/jackson-core) Copyright 2013 FasterXML.
 * 7. JSON (https://www.json.org/license.html) Copyright 2002 JSON.org.
 * 8. Apache Commons (https://commons.apache.org/) Copyright 2004 The Apache Software Foundation.
 * 9. RuleBook (https://github.com/deliveredtechnologies/rulebook/blob/develop/LICENSE.txt) Copyright 2020 Delivered Technologies.
 * 10. SLF4J (http://www.slf4j.org/license.html) Copyright 2004-2017 QOS.ch.
 * 11. Eclipse Jetty (https://www.eclipse.org/jetty/licenses.html) Copyright 1995-2020 Mort Bay Consulting Pty Ltd and others..
 * 12. Mockito (https://github.com/mockito/mockito/wiki/License) Copyright 2007 Mockito contributors.
 * 13. SubEtha SMTP (https://github.com/voodoodyne/subethasmtp) Copyright 2006-2007 SubEthaMail.org.
 * 14. JSch - Java Secure Channel (http://www.jcraft.com/jsch/) Copyright 2002-2015 Atsuhiko Yamanaka, JCraft,Inc. .
 * 15. ouimeaux (https://github.com/iancmcc/ouimeaux) Copyright 2014 Ian McCracken.
 * 16. Flask (https://github.com/pallets/flask) Copyright 2010 Pallets.
 * 17. Flask-RESTful (https://github.com/flask-restful/flask-restful) Copyright 2013 Twilio, Inc..
 * 18. libvirt-python (https://github.com/libvirt/libvirt-python) Copyright 2016 RedHat, Fedora project.
 * 19. Requests: HTTP for Humans (https://github.com/psf/requests) Copyright 2019 Kenneth Reitz.
 * 20. netifaces (https://github.com/al45tair/netifaces) Copyright 2007-2018 Alastair Houghton.
 * 21. ipaddress (https://github.com/phihag/ipaddress) Copyright 2001-2014 Python Software Foundation.
 * DM20-0543
 *
 */
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
    private final List<NeoSensor> sensors = new ArrayList<NeoSensor>();

    // NOTE: default values are "udooer" and "udooer".
    private final String username;
    private final String password;

    public Monitor(Device device){
        super(device);
        String[] credentials = device.getCredentials().split(":");
        if(credentials.length < 2) {
            throw new RuntimeException("Device credentials are not in the expected format.");
        }
        username = credentials[0];
        password = credentials[1];
        setSensors();
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
