package Monitors;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public abstract class IotMonitor {

    protected Timer pollTimer = new Timer();
    protected int pollInterval = 5000;
    protected boolean pollingEnabled = true;
    protected boolean timerGoing = false;

    protected Logger logger;

    public IotMonitor(){
        logger = Logger.getLogger( "myLogger" );
    }

    /**
     * Polls the device for updates.
     */
    public abstract void pollDevice();

    /**
     * Saves the current state of the iot device to the database
     */
    public abstract void saveCurrentState();

    /**
     * Connect to the device and begin monitoring.
     */
    public void start(){
        if(pollingEnabled){
            startPolling(pollInterval);
        }
    }

    /**
     * Sets whether or not the device should poll the device for updates.
     * @param isEnabled If true, the device will begin being polled for updates.
     */
    public void setPollingUpdates(boolean isEnabled) {
        pollingEnabled = isEnabled;
        if (isEnabled){
            startPolling(pollInterval);
        }
        else {
            stopPolling();
        }
    }

    /**
     * Starts a task to poll the device for its current state.
     * Polling interval is controlled by pollInterval.
     * Can be cancelled with stopPolling
     */
    protected void startPolling(int pollInterval) {
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
            pollDevice();
            saveCurrentState();
        }
    }

    /**
     * Sets the interval for polling the device for updates.
     * @param newInterval new interval, in seconds.
     */
    public void setPollInterval(int newInterval) {
        pollInterval = newInterval;
    }
}
