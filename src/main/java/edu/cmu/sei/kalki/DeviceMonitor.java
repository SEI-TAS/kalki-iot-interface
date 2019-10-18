package edu.cmu.sei.kalki;

import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.kalki.Monitors.IotMonitor;
import edu.cmu.sei.kalki.Monitors.PollingMonitor;
import edu.cmu.sei.ttg.kalki.models.StageLog;

import java.util.HashMap;
import java.util.logging.Logger;

public class DeviceMonitor {
    private Logger logger = Logger.getLogger("iot-interface");
    private HashMap<Integer, IotMonitor> monitors;
    private String apiUrl;

    public DeviceMonitor(String url) {
        apiUrl = url;
        monitors = new HashMap<Integer, IotMonitor>();
    }

    public String getApiUrl(){
        return apiUrl;
    }

    /**
     * Starts a new monitor for the given device
     * @param device
     */
    public void startMonitor(Device device) {
        if(device.getSamplingRate() == 0){
            logger.info("[DeviceMonitor] Sampling rate of 0. Not starting monitor.");
        }
        else {
            logger.info("[DeviceMonitor] Starting monitor for device: "+device.getId());
            IotMonitor mon = IotMonitor.fromDevice(device, apiUrl);
            monitors.put(device.getId(), mon);
        }
    }

    /**
     * Updates the sampling rate for the given device
     * @param device
     */
    public void updateMonitor(Device device) {
        IotMonitor mon = monitors.get(device.getId());
        if(mon != null && mon.getPollInterval() != device.getSamplingRate()){
            logger.info("[DeviceMonitor] Updating monitor for device: "+device.getId());
            if(mon.isPollable() && device.getSamplingRate() > 0){
                logger.info("[DeviceMonitor] Found monitor, updating sampling rate");
                mon.setPollInterval(device.getSamplingRate());
                monitors.replace(device.getId(), mon);
                logUpdateMonitor(device);
            }
        } else {
            logger.severe("[DeviceMonitor] No monitor found for given device "+device.getId()+". Starting one...");
            startMonitor(device);
        }
    }

    /**
     * Sends a StageLog to the DeviceControllerApi to record updating a sampling rate
     * @param device Device the monitor was updated for
     */
    private void logUpdateMonitor(Device device) {
        logger.info("[DeviceMonitor] Logging monitor update for device: "+device.getId());
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.INCREASE_SAMPLE_RATE, StageLog.Stage.FINISH, "Increased sampling rate for device: "+device.getId());
        DeviceControllerApi.sendLog(log, apiUrl);
    }


}
