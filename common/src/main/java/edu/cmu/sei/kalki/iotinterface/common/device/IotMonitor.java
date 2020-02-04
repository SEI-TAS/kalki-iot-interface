package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.*;

import java.util.logging.Logger;

public abstract class IotMonitor {
    protected int deviceId;
    protected String deviceIp;
    protected boolean isPollable;

    protected final Logger logger = Logger.getLogger("iot-interface");

    public IotMonitor(int deviceId, String deviceIp, boolean isPollable){
        this.deviceId = deviceId;
        this.deviceIp = deviceIp;
        this.isPollable = isPollable;
    }

    public boolean isPollable() {
        return isPollable;
    }

    protected void sendToDeviceController(DeviceStatus status) {
        DeviceControllerApi.sendStatus(status);
    }
}
