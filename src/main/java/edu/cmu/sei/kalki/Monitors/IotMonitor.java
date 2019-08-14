package edu.cmu.sei.kalki.Monitors;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.logging.Logger;

import edu.cmu.sei.ttg.kalki.models.*;
import org.json.JSONObject;

public abstract class IotMonitor {
//    protected final String apiUrl = "http://10.27.151.103:9090/device-controller-api/new-status"; // test url
    protected final String apiUrl = "http://10.27.153.3:9090/device-controller-api/new-status"; // deployment url
    protected Timer pollTimer = new Timer();
    protected int pollInterval;
    protected boolean isPollable;
    protected boolean pollingEnabled = true;
    protected boolean timerGoing = false;
    public int deviceId;

    protected Logger logger;

    public IotMonitor(){
        logger = Logger.getLogger( "iot-interface" );
    }

    public static IotMonitor fromDevice(Device device){
        IotMonitor mon = null;
        if(device.getType().getId() == 1){ //Dlink Camera
            mon = new DLinkMonitor(device.getId());
        }
        else if(device.getType().getId() == 2){ //Undoo Neo
            mon = new NeoMonitor(device.getId(), device.getIp(), device.getSamplingRate());
        }
        else if (device.getType().getId() == 3){ //WeMo Insight
            mon = new WemoMonitor(device.getId(), device.getName(), device.getSamplingRate());
        }
        else if(device.getType().getId() == 4){ //Hue Light
            mon = new HueMonitor(device.getIp(), 80, device.getId(), device.getSamplingRate());
        }
        return mon;
    }

    public boolean isPollable() {
        return isPollable;
    }

    public void setPollInterval(int pollInterval) {
        return;
    }

    protected void sendToDeviceController(DeviceStatus status) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            JSONObject json = new JSONObject(status.toString());
            out.write(json.toString());
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            logger.severe("[IoTMonitor] Error sending status to DeviceController: "+status.toString());
            logger.severe(e.getMessage());
        }
    }

}
