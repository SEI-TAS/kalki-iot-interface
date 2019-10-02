package edu.cmu.sei.kalki.Monitors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Logger;

import edu.cmu.sei.ttg.kalki.models.*;
import org.json.JSONObject;

public abstract class IotMonitor {
    protected String apiUrl;
    protected Timer pollTimer = new Timer();
    protected int pollInterval;
    protected boolean isPollable;
    protected boolean pollingEnabled = true;
    protected boolean timerGoing = false;
    public int deviceId;

    protected final Logger logger = Logger.getLogger("iot-interface");

    public IotMonitor(){ }

    public static IotMonitor fromDevice(Device device, String apiUrl){
        Logger logger = Logger.getLogger("iot-interface");
        try {
            String classPath = "edu.cmu.sei.kalki.Monitors."+getDeviceTypeMonitorClassName(device.getType().getName());
            Constructor con = Class.forName(classPath).getConstructor(Integer.TYPE, String.class, Integer.TYPE, String.class);
            IotMonitor mon = (IotMonitor) con.newInstance(device.getId(), device.getIp(), device.getSamplingRate(), apiUrl);
            return mon;
        } catch (Exception e2){
            e2.printStackTrace();
            logger.info(e2.getMessage());

            return null;
        }
    }

    /**
     * Removes spaces from device type's name and append 'Monitor'
     * @param devTypeName
     * @return device type's monitor class name
     */
    private static String getDeviceTypeMonitorClassName(String devTypeName) {
        String[] temp = devTypeName.split(" ");
        String name = "";
        for(int i=0;i<temp.length;i++){
            name+=temp[i];
        }
        name+="Monitor";
        return name;
    }

    public boolean isPollable() {
        return isPollable;
    }

    public void setPollInterval(int pollInterval) {
        return;
    }

    protected void sendToDeviceController(DeviceStatus status) {
        try {
            JSONObject json = new JSONObject(status.toString());
            URL url = new URL(apiUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[IoTMonitor] Error sending status to DeviceController: "+status.toString());
            logger.severe(e.getMessage());
        }
    }

}
