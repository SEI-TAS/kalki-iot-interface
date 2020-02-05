package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.*;

import java.util.logging.Logger;

public abstract class IotMonitor {
    protected Device device;
    protected boolean isPollable;

    protected final Logger logger = Logger.getLogger("iot-interface");

    public IotMonitor(Device device, boolean isPollable){
        this.device = device;
        this.isPollable = isPollable;
    }

    public boolean isPollable() {
        return isPollable;
    }

    protected void sendToDeviceController(DeviceStatus status) {
        DeviceControllerApi.sendStatus(status);
    }
}
