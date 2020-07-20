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
package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.iotinterface.common.device.IotMonitor;
import edu.cmu.sei.kalki.db.models.StageLog;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Logger;

public class MonitorManager {
    private Logger logger = Logger.getLogger("iot-interface");
    private HashMap<Integer, IotMonitor> monitors;
    private static final String className = "[MonitorManager] ";

    public MonitorManager() {
        monitors = new HashMap<>();
    }

    /**
     * Starts a new monitor for the given device
     * @param device
     */
    public void startMonitor(Device device) {
        if(device.getSamplingRate() == 0){
            logger.info(className + "Sampling rate of 0. Not starting monitor.");
            logSamplingRateChange(device, "0 sampling rate, monitor not started");
        }
        else {
            logger.info(className + "Starting monitor for device: "+device.getId());
            IotMonitor mon = fromDevice(device);
            if(mon == null) {
                logger.info(className + "Monitor class not found for device " + device.getName() + " of type " + device.getType().getName());
            } else {
                monitors.put(device.getId(), mon);
                mon.start();
                logger.info(className +  "Monitor started for device " + device.getName());
                logSamplingRateChange(device, "Monitor started with initial sampling rate");
            }
        }
    }

    /**
     * Stops a running monitor for the given device
     * @param device
     */
    public void stopMonitor(Device device) {
        if(monitors.containsKey(device.getId())) {
            IotMonitor monitor = monitors.get(device.getId());
            monitor.stop();
        }
        else {
            logger.info(className + " Can't stop monitor; it was not found for device " + device.getName());
        }
    }

    /**
     * Updates the sampling rate for the given device
     * @param device
     */
    public void updateMonitor(Device device) {
        IotMonitor mon = monitors.get(device.getId());
        if(mon != null){ // monitor exists

            if(!mon.isPollable()){ // monitor doesn't have a sampling rate
                logger.info((className + "Monitor is not pollable, no sampling rate to update"));
                return;
            }

            PollingMonitor pollMon = (PollingMonitor) mon;
            if(pollMon.getPollIntervalMs() != device.getSamplingRate()) { // the sampling rate has been updated
                logger.info(className + " Updating monitor for device: "+device.getId());
                pollMon.setPollIntervalMs(device.getSamplingRate());
                monitors.replace(device.getId(), pollMon);
            } else {
                logger.info(className + " Not updating monitor for device: "+device.getId()+". Sampling rate hasn't changed.");
            }

        } else {
            logger.info(className + " No monitor found for given device "+device.getId()+". Starting one...");
            startMonitor(device);
        }
    }

    /**
     * Creates an instance of an IotMonitor for the given device's type
     * @param device The device to be monitored
     * @return The instance of the device's monitor
     */
    public static IotMonitor fromDevice(Device device){
        Logger logger = Logger.getLogger("iot-interface");
        try {
            // Remove white spaces from device type name
            String deviceTypeName = device.getType().getName().replaceAll("\\s+","");

            // Get IotMonitor constructor via reflection
            String classPath = "edu.cmu.sei.kalki.iotinterface.plugins." + deviceTypeName + ".Monitor";
            Constructor con = Class.forName(classPath).getConstructor(Device.class);

            // Create and return instance of specific IotMonitor
            IotMonitor mon = (IotMonitor) con.newInstance(device);
            return mon;
        } catch (Exception e){
            logger.info("Error creating monitor from device type: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a StageLog to the DeviceControllerApi to record updating a sampling rate
     * @param device Device the monitor was updated for
     */
    private void logSamplingRateChange(Device device, String info) {
        if(device.getCurrentState() != null) {
            logger.info( className + " Logging monitor sampling rate change for device: "+device.getId());
            StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.FINISH, info);
            DeviceControllerApi.sendLog(log);
        }
        else {
            logger.info( className + " Not logging sampling rate change (no current state id) for device: "+device.getId());
        }
    }
}
