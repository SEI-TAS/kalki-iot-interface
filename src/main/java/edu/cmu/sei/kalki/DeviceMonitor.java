package edu.cmu.sei.kalki;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.kalki.Monitors.IotMonitor;
import edu.cmu.sei.kalki.Monitors.PollingMonitor;

import java.util.HashMap;

public class DeviceMonitor {
    private HashMap<Integer, IotMonitor> monitors;

    public DeviceMonitor() {
        monitors = new HashMap<Integer, IotMonitor>();
    }

    /**
     * Starts a new monitor for the given device
     * @param device
     */
    public void startMonitor(Device device) {
        System.out.println("Starting monitor for device: "+device.getId());
        IotMonitor mon = IotMonitor.fromDevice(device);
        monitors.put(device.getId(), mon);
    }

    /**
     * Updates the sampling rate for the given device
     * @param device
     */
    protected void updateMonitor(Device device) {
        System.out.println("Updating monitor for device: "+device.getId());
        IotMonitor mon = monitors.get(device.getId());
        if(mon.isPollable()){
            System.out.println("Found monitor, updating sampling rate");
            mon.setPollInterval(device.getSamplingRate());
            monitors.replace(device.getId(), mon);
        }
    }
}
