package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.Timer;
import java.util.TimerTask;

public abstract class PollingMonitor extends IotMonitor {
    private static final String LOG_ID = "[PollingMonitor]";

    protected int pollIntervalMs;
    protected Timer pollTimer;
    private boolean timerGoing = false;

    public PollingMonitor(Device device) {
        super(device);
        this.isPollable = true;
        this.pollIntervalMs = this.device.getSamplingRate();
    }

    /**
     * Polls the device for updates. Adds all device attributes to status.
     */
    public abstract void pollDevice(DeviceStatus Status);

    /**
     * Saves the current state of the iot device to the database
     */
    public void sendStatusToDB(DeviceStatus status){
        sendToDeviceController(status);
        logger.info("Sent status to device controller:" + status.toString());
    }

    /**
     * Connect to the device and begin monitoring.
     */
    public void start(){
        logger.info(LOG_ID + " Starting monitor for device " + device.getName());
        startPolling();
    }

    /**
     * Connect to the device and stop monitoring.
     */
    public void stop(){
        logger.info(LOG_ID + " Stopping monitor for device " + device.getName());
        stopPolling();
    }

    /**
     * Starts a task to poll the device for its current state.
     * Polling interval is controlled by pollInterval.
     * Can be cancelled with stopPolling
     */
    protected void startPolling() {
        pollTimer = new Timer();
        pollTimer.schedule(new PollTask(device.getId()), pollIntervalMs, pollIntervalMs);
        timerGoing = true;
        logger.info(LOG_ID + " Monitor started for device " + device.getName());
    }

    /**
     * Stops the current polling task if there is one.
     */
    protected void stopPolling() {
        if (timerGoing){
            pollTimer.cancel();
            timerGoing = false;
            pollTimer = null;
            logger.info(LOG_ID + " Monitor stopped for device " + device.getName());
        }
        else {
            logger.info(LOG_ID + " Monitor was not polling for device " + device.getName());
        }
    }

    /**
     * Class for polling the device at a set interval.
     * Started from startPolling
     */
    class PollTask extends TimerTask {
        private final int deviceId;

        public PollTask(int deviceId){
            this.deviceId = deviceId;
        }

        public void run() {
            DeviceStatus status = new DeviceStatus(this.deviceId);
            pollDevice(status); // pollDevice adds attributes to currentStatus
            sendStatusToDB(status);
        }
    }

    /**
     * Sets the interval for polling the device for updates.
     * @param newIntervalMs new interval, in milliseconds.
     */
    public void setPollIntervalMs(int newIntervalMs) {
        pollIntervalMs = newIntervalMs;
        stopPolling();
        startPolling();
    }

    public int getPollIntervalMs() {
        return pollIntervalMs;
    }

}
