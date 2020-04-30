package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.kalki.db.models.*;

import java.util.logging.Logger;

public abstract class IotMonitor {
    protected Device device;
    protected boolean isPollable = false;

    protected final Logger logger = Logger.getLogger("iot-interface");

    public IotMonitor(Device device) {
        this.device = device;
    }

    public boolean isPollable() {
        return isPollable;
    }

    /**
     * Begin monitoring.
     */
    public abstract void start();

    /**
     * Stop monitoring.
     */
    public abstract void stop();

    protected void sendToDeviceController(DeviceStatus status) {
        DeviceControllerApi.sendStatus(status);
    }
}
