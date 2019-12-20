package edu.cmu.sei.kalki.Monitors;

import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.logging.Logger;

import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.*;

public abstract class IotMonitor {
    protected String apiUrl;
    protected Timer pollTimer = new Timer();
    protected int pollInterval;
    protected boolean isPollable;
    protected boolean pollingEnabled = true;
    protected boolean timerGoing = false;
    protected int deviceId;

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
        this.pollInterval = pollInterval;
    }

    public int getPollInterval() {
        return pollInterval;
    }


    protected void sendToDeviceController(DeviceStatus status) {
        DeviceControllerApi.sendStatus(status, apiUrl);
    }

}
