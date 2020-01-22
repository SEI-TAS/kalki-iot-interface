package edu.cmu.sei.kalki.utils;

import edu.cmu.sei.ttg.kalki.models.*;

import java.util.Timer;
import java.util.logging.Logger;

public abstract class IotMonitor {
    protected String apiUrl;
    protected Timer pollTimer = new Timer();
    protected int pollInterval;
    protected boolean isPollable;
    protected boolean timerGoing = false;
    protected int deviceId;

    protected final Logger logger = Logger.getLogger("iot-interface");

    public IotMonitor(){ }

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
