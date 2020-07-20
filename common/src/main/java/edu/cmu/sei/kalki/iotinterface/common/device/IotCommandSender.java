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

import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import edu.cmu.sei.kalki.db.models.StageLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

public abstract class IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String LOG_ID = "[IotCommandSender] ";

    protected Device device;
    private List<DeviceCommand> commands;

    public IotCommandSender(Device device, List<DeviceCommand> commands){
        this.device = device;
        this.commands = commands;
    }

    public void sendCommands() {
        for(DeviceCommand command: commands) {
            sendCommand(command);
        }
    }

    /**
     * Method that uses reflection to call a method of the derived classes with the same name as the command that needs to be sent.
     */
    private void sendCommand(DeviceCommand command) {
        // Dashes are not valid in method names.
        String methodName = "command_" + command.getName().replace("-", "_");
        logger.info(LOG_ID + " Looking for method named " + methodName);

        // Get the method that implements this command. This will only work if used in derived classes.
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            logger.warning("Method " + methodName + " not found.");
        }

        // Execute the actual method, and send a notification that it worked.
        if(method != null) {
            logger.info(LOG_ID + "Sending '" + command.getName() + "' command to device of type " + device.getType().getName() + ": " + device.getName() + "(id: " + device.getId() + ")");
            try {
                method.invoke(this);
                logSendCommand(command.getName());
            }
            catch (IllegalAccessException e) {
                logger.severe(LOG_ID + "Illegal access trying to access method: " + e.toString());
            }
            catch (InvocationTargetException e) {
                logger.severe(LOG_ID + "Invocation target error trying to access method: " + e.getCause().toString());
                e.getCause().printStackTrace();
            }
        }
        else {
            logger.severe(LOG_ID + "Command: " + command.getName() + " not supported for " + device.getType().getName());
        }
    }

    /**
     * Sends StageLog to Device Controller indicating a command was sent to the device
     * @param command The name of the command
     */
    private void logSendCommand(String command) {
        logger.info(LOG_ID + " Logging that a command was sent to the device.");
        if(device.getCurrentState() != null)
        {
            StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: " + command);
            DeviceControllerApi.sendLog(log);
        }
    }
}
