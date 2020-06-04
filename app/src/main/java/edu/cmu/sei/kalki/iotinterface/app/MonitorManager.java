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
