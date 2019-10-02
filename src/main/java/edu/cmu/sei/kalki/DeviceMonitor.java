package edu.cmu.sei.kalki;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.kalki.Monitors.IotMonitor;
import edu.cmu.sei.kalki.Monitors.PollingMonitor;

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

    /**
     * Starts a new monitor for the given device
     * @param device
     */
    public void startMonitor(Device device) {
        logger.info("[DeviceMonitor] Starting monitor for device: "+device.getId());
        IotMonitor mon = IotMonitor.fromDevice(device, apiUrl);
        monitors.put(device.getId(), mon);
    }

    /**
     * Updates the sampling rate for the given device
     * @param device
     */
    public void updateMonitor(Device device) {
        IotMonitor mon = monitors.get(device.getId());
        if(mon != null){
            logger.info("[DeviceMonitor] Updating monitor for device: "+device.getId());
            if(mon.isPollable()){
                logger.info("[DeviceMonitor] Found monitor, updating sampling rate");
                mon.setPollInterval(device.getSamplingRate());
                monitors.replace(device.getId(), mon);
            }
        } else {
            logger.severe("[DeviceMonitor] No monitor found for given device "+device.getId()+". Starting one...");
            startMonitor(device);
        }

    }
}
