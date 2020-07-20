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
package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import edu.cmu.sei.kalki.db.utils.CommandExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WemoScript
{
    private static final String LOG_ID = "[WemoScript] ";
    private static final String WEMO_SCRIPT_FOLDER = "../temp/wemo/";

    private static Logger logger = Logger.getLogger("iot-interface");

    /**
     * Configures command line arguments to execute the wemo python script
     * @param deviceIp The ip of the WemoInsight device
     * @param command The command to be sent. Options: turn-off, turn-on, status (not applicable here)
     * @return Array of command line args to execute the script
     */
    private static List<String> setParams(String deviceIp, String command) throws IOException
    {
        logger.info(LOG_ID + "Preparing script");
        List<String> params = new ArrayList<>();
        params.add("python");
        params.add("-m");
        params.add("pipenv");
        params.add("run");
        params.add("python");
        params.add("wemo.py");
        params.add(deviceIp);
        params.add(command);
        return params;
    }

    /**
     * Executes the wemo python script and process output
     * @param command The string that is the command's name
     */
    public static String executeScript(String command, String deviceIp){
        try {
            List<String> params = setParams(deviceIp, command);
            List<String> outputs = CommandExecutor.executeCommand(params, WEMO_SCRIPT_FOLDER);

            StringBuilder sb = new StringBuilder();
            for (String s : outputs)
            {
                sb.append(s);
                sb.append("\n");
            }

            return sb.toString();

        } catch (IOException e) {
            logger.severe(LOG_ID + " Error reading response from device " + deviceIp);
            logger.severe(e.getMessage());
            return null;
        }
    }
}
