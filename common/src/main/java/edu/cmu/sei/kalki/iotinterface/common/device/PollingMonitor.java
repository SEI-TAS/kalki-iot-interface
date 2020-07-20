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
package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.Timer;
import java.util.TimerTask;

public abstract class PollingMonitor extends IotMonitor {
    private static final String LOG_ID = "[PollingMonitor]";

    protected int pollIntervalMs;
    protected Timer pollTimer;
    private boolean timerGoing = false;

    public PollingMonitor(Device device) {
        super(device);
        this.isPollable = true;
        this.pollIntervalMs = this.device.getSamplingRate();
    }

    /**
     * Polls the device for updates. Adds all device attributes to status.
     */
    public abstract void pollDevice(DeviceStatus Status);

    /**
     * Saves the current state of the iot device to the database
     */
    public void sendStatusToDB(DeviceStatus status){
        sendToDeviceController(status);
        logger.info("Sent status to device controller:" + status.toString());
    }

    /**
     * Connect to the device and begin monitoring.
     */
    public void start(){
        logger.info(LOG_ID + " Starting monitor for device " + device.getName());
        startPolling();
    }

    /**
     * Connect to the device and stop monitoring.
     */
    public void stop(){
        logger.info(LOG_ID + " Stopping monitor for device " + device.getName());
        stopPolling();
    }

    /**
     * Starts a task to poll the device for its current state.
     * Polling interval is controlled by pollInterval.
     * Can be cancelled with stopPolling
     */
    protected void startPolling() {
        pollTimer = new Timer();
        pollTimer.schedule(new PollTask(device.getId()), pollIntervalMs, pollIntervalMs);
        timerGoing = true;
        logger.info(LOG_ID + " Monitor started for device " + device.getName());
    }

    /**
     * Stops the current polling task if there is one.
     */
    protected void stopPolling() {
        if (timerGoing){
            pollTimer.cancel();
            timerGoing = false;
            pollTimer = null;
            logger.info(LOG_ID + " Monitor stopped for device " + device.getName());
        }
        else {
            logger.info(LOG_ID + " Monitor was not polling for device " + device.getName());
        }
    }

    /**
     * Class for polling the device at a set interval.
     * Started from startPolling
     */
    class PollTask extends TimerTask {
        private final int deviceId;

        public PollTask(int deviceId){
            this.deviceId = deviceId;
        }

        public void run() {
            try {
                DeviceStatus status = new DeviceStatus(this.deviceId);
                pollDevice(status); // pollDevice adds attributes to currentStatus
                sendStatusToDB(status);
            } catch(Exception e) {
                logger.severe(LOG_ID + "Error polling device information: " + e.toString());
            }
        }
    }

    /**
     * Sets the interval for polling the device for updates.
     * @param newIntervalMs new interval, in milliseconds.
     */
    public void setPollIntervalMs(int newIntervalMs) {
        pollIntervalMs = newIntervalMs;
        stopPolling();
        startPolling();
    }

    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

}
