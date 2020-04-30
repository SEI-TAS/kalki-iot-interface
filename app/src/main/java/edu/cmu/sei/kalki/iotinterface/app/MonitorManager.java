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
    private static final String className = "[MonitorManager]";

    public MonitorManager() {
        monitors = new HashMap<>();
    }

    /**
     * Starts a new monitor for the given device
     * @param device
     */
    public void startMonitor(Device device) {
        if(device.getSamplingRate() == 0){
            logger.info(className + " Sampling rate of 0. Not starting monitor.");
            logUpdateMonitor(device, "0 sampling rate");
        }
        else {
            logger.info(className + " Starting monitor for device: "+device.getId());
            IotMonitor mon = fromDevice(device);
            monitors.put(device.getId(), mon);
            logUpdateMonitor(device, "Monitor started");
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
            if(pollMon.getPollInterval() != device.getSamplingRate()) { // the sampling rate has been updated
                logger.info(className + " Updating monitor for device: "+device.getId());
                pollMon.setPollInterval(device.getSamplingRate());
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
            String classPath = "edu.cmu.sei.kalki.devicetypes."+deviceTypeName+".Monitor";
            Constructor con = Class.forName(classPath).getConstructor(Integer.TYPE, String.class, Integer.TYPE);

            // Create and return instance of specific IotMonitor
            IotMonitor mon = (IotMonitor) con.newInstance(device.getId(), device.getIp(), device.getSamplingRate());
            return mon;
        } catch (Exception e2){
            e2.printStackTrace();
            logger.info(e2.getMessage());

            return null;
        }
    }

    /**
     * Sends a StageLog to the DeviceControllerApi to record updating a sampling rate
     * @param device Device the monitor was updated for
     */
    private void logUpdateMonitor(Device device, String info) {
        logger.info( className + " Logging monitor update for device: "+device.getId());
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.FINISH, info);
        DeviceControllerApi.sendLog(log);
    }
}
