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
package edu.cmu.sei.kalki.iotinterface.app.api;

import edu.cmu.sei.kalki.db.models.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Base class for ApiServlets to handle requests from the DeviceController
 */
public class ApiServlet extends HttpServlet {

    /**
     * Method to extract body of an HTTP request and convert it to a JSON object
     * @param request
     * @param response
     * @return
     * @throws ServletException
     */
    protected JSONObject parseRequestBody(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject requestBody;
        try {
            String bodyLine;
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader bodyReader = request.getReader();
            while((bodyLine = bodyReader.readLine()) != null) {
                jsonBody.append(bodyLine);
            }
            requestBody = new JSONObject(jsonBody.toString());
        }
        catch (JSONException e) {
            throw new ServletException("Error parsing body JSON of request: " + e.getMessage());
        }
        catch (IOException e) {
            throw new ServletException("Error parsing body of request: " + e.getMessage());
        }

        return requestBody;
    }

    /**
     * Method to convert a JSONObject to a Device
     * @param deviceData
     * @return
     * @throws JSONException
     */
    protected Device parseDevice(JSONObject deviceData) throws JSONException {
        int id = deviceData.getInt("id");
        String name = deviceData.getString("name");
        String description = deviceData.getString("description");
        DeviceType deviceType = deviceData.optJSONObject("type")!=null ? parseDeviceType(deviceData.getJSONObject("type")):null;
        Group group = deviceData.optJSONObject("group")!=null ? parseGroup(deviceData.getJSONObject("group")):null;
        String ip = deviceData.getString("ip");
        int statusHistorySize = deviceData.getInt("statusHistorySize");
        int samplingRate = deviceData.getInt("samplingRate");
        int defaultSamplingRate = deviceData.getInt("defaultSamplingRate");
        DeviceSecurityState currentState = deviceData.optJSONObject("currentState")!=null ? parseSecurityState(deviceData.getJSONObject("currentState")):null;
        Alert lastAlert = deviceData.optJSONObject("lastAlert")!=null ? parseAlert(deviceData.getJSONObject("lastAlert")):null;
        DataNode datNode = deviceData.optJSONObject("dataNode")!=null ? parseDataNode(deviceData.getJSONObject("dataNode")):null;
        Device device = new Device(name, description, deviceType, group, ip, statusHistorySize, samplingRate, defaultSamplingRate,currentState, lastAlert, datNode);
        device.setId(id);
        return device;
    }

    /**
     * Method to convert a JSONObject to a DeviceType
     * @param type
     * @return
     */
    protected DeviceType parseDeviceType(JSONObject type) {
        int id = type.getInt("id");
        String name = type.getString("name");
        return new DeviceType(id, name);
    }

    /**
     * Method to convert a JSONObject to a Group
     * @param group
     * @return
     */
    protected Group parseGroup(JSONObject group) {
        int id = group.getInt("id");
        String name = group.getString("name");
        return new Group(id, name);
    }

    /**
     * Method to convert a JSONObject to a DeviceSecurityState
     * @param state
     * @return
     */
    protected DeviceSecurityState parseSecurityState(JSONObject state) {
        int id = state.getInt("id");
        int deviceId = state.getInt("deviceId");
        int stateId = state.getInt("stateId");
        Timestamp timestamp = new Timestamp(state.getLong("timestamp"));
        String name = state.getString("name");
        return new DeviceSecurityState(id, deviceId, stateId, timestamp, name);
    }

    /**
     * Method to convert a JSONObject to an Alert
     * @param alert
     * @return
     */
    protected Alert parseAlert(JSONObject alert) {
        int id = alert.getInt("id");
        String name = alert.getString("name");
        Timestamp timestamp = new Timestamp(alert.getLong("timestamp"));
        String alerterId = alert.getString("alerterId");
        int deviceId = alert.getInt("deviceId");
        Integer deviceStatusId = alert.getInt("deviceStatusId");
        int alertTypeId = alert.getInt("alertTypeId");
        String info = alert.getString("info");
        return new Alert(id, name, timestamp, alerterId, deviceId, deviceStatusId, alertTypeId, info);
    }

    /**
     * Method to convert a JSONObject to an DataNode
     * @param dataNode
     * @return
     */
    protected DataNode parseDataNode(JSONObject dataNode) {
        int id = dataNode.getInt("id");
        String name = dataNode.getString("name");
        String ipAddress = dataNode.getString("ipAddress");
        return new DataNode(id, name, ipAddress);
    }
}
