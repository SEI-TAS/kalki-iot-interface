package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import java.util.Timer;
import java.util.TimerTask;

public abstract class PollingMonitor extends IotMonitor {
    private static final String LOG_ID = "[PollingMonitor]";

    protected int pollInterval;
    protected Timer pollTimer;
    private boolean timerGoing;

    public PollingMonitor(Device device, boolean isPollable, int pollInterval) {
        super(device, isPollable);
        this.pollInterval = pollInterval;
        this.pollTimer = new Timer();
        this.timerGoing = false;
    }

    /**
     * Polls the device for updates. Adds all device attributes to status.
     */
    public abstract void pollDevice(DeviceStatus Status);

    /**
     * Saves the current state of the iot device to the database
     */
    public void saveCurrentState(DeviceStatus status){
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
        pollTimer.schedule(new PollTask(device.getId()), pollInterval, pollInterval);
        timerGoing = true;
    }

    /**
     * Stops the current polling task if there is one.
     */
    protected void stopPolling() {
        if (timerGoing){
            logger.info(LOG_ID + " Stopping monitor for device " + device.getName());
            pollTimer.cancel();
        }
    }

    /**
     * Class for polling the device at a set interval.
     * Started from startPolling
     */
    class PollTask extends TimerTask {
        private int deviceId;

        public PollTask(int deviceId){
            this.deviceId = deviceId;
        }

        public void run() {
            DeviceStatus status = new DeviceStatus(this.deviceId);
            pollDevice(status); // pollDevice adds attributes to currentStatus
            saveCurrentState(status);
        }
    }

    /**
     * Sets the interval for polling the device for updates.
     * @param newInterval new interval, in milliseconds.
     */
    public void setPollInterval(int newInterval) {
        pollInterval = newInterval;
        stopPolling();
        startPolling();
    }

    public int getPollInterval() {
        return pollInterval;
    }

}
