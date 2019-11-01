package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

// SSH imports
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import org.json.JSONException;
import org.json.JSONObject;

public class RoombaMonitor extends PollingMonitor {

    private String ip;
    private int deviceId;
    private DeviceStatus status;

    private Map<String, String> attributes = new HashMap<String, String>();

    public RoombaMonitor(int deviceId, String ip, String username, String password, int samplingRate, String url){
        this(deviceId, ip, samplingRate, url);
        this.isPollable = true;
    }

    public RoombaMonitor(int deviceId, String ip, int samplingRate, String url){
        super();
        logger.info("[RoombaMonitor] Starting Neo Monitor for device: " + deviceId);
        this.ip = ip;
        this.pollInterval = samplingRate;
        this.isPollable = true;
        this.apiUrl = url;
        start();
    }

    public String issueCommand(String command){
        try {
            String[] cmdArray = new String[2];
            cmdArray[0] = "node";
            cmdArray[1] = "scripts/roomba" + command + ".js";

            // create a process and execute cmdArray and correct environment
            Process process = Runtime.getRuntime().exec(cmdArray,null);

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            String line = null;
            String message = "";

            while ((line = input.readLine()) != null)
            {
                System.out.println(line);
                message += line;
            }
            return message;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void getPollInfo(){
        String response = issueCommand("Poll");
        logger.info(response);
        try {

            JSONObject schedule = new JSONObject(response).getJSONObject("schedule");
            JSONObject mission = new JSONObject(response).getJSONObject("mission");


            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK) - 1; //puts sunday at 0

            attributes.put("daily_hour", Integer.toString(schedule.getJSONArray("h").getInt(day)));
            attributes.put("daily_minute", Integer.toString(schedule.getJSONArray("m").getInt(day)));
            attributes.put("daily_cycle", schedule.getJSONArray("cycle").getString(day));
            attributes.put("current_cycle", mission.getJSONObject("cleanMissionStatus").getString("cycle"));
        }
        catch(JSONException e){
            logger.severe("Error getting information from roomba");
            attributes = new HashMap<String, String>();
        }

    }

    public void dockRoomba(){
        issueCommand("Dock");
    }

    @Override
    public void pollDevice() {
        attributes = new HashMap<String, String>();
        getPollInfo();
    }

    @Override
    public void saveCurrentState() {
        logger.info("[RoombaMonitor] Saving current state");
        status = new DeviceStatus(deviceId, attributes);
        sendToDeviceController(status);
    }
}
