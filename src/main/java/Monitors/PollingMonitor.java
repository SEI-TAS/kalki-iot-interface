package Monitors;

import java.util.Timer;
import java.util.TimerTask;

public abstract class PollingMonitor extends IotMonitor {

    /**
     * Polls the device for updates.
     */
    public abstract void pollDevice();

    /**
     * Saves the current state of the iot device to the database
     */
    public abstract void saveCurrentState();

    /**
     * Checks if the latest status generates an alert
     */
    public abstract void runAlertRules();

    /**
     * Connect to the device and begin monitoring.
     */
    public void start(){
        logger.info("Starting monitor!");
        startPolling();
    }

    /**
     * Starts a task to poll the device for its current state.
     * Polling interval is controlled by pollInterval.
     * Can be cancelled with stopPolling
     */
    protected void startPolling() {
        pollTimer = new Timer();
        pollTimer.schedule(new PollTask(), pollInterval, pollInterval);
        timerGoing = true;
    }

    /**
     * Stops the current polling task if there is one.
     */
    protected void stopPolling() {
        if (timerGoing){
            pollTimer.cancel();
        }
    }

    /**
     * Class for polling the device at a set interval.
     * Started from startPolling
     */
    class PollTask extends TimerTask {
        public void run() {
//            pollDevice();
//            saveCurrentState();
            runAlertRules();
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
}
