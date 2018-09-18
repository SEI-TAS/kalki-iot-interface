package Monitors;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import kalkidb.models.*;

public abstract class IotMonitor {

    protected Timer pollTimer = new Timer();
    protected int pollInterval;
    protected boolean pollingEnabled = true;
    protected boolean timerGoing = false;
    public int deviceId;

    protected Logger logger;

    public IotMonitor(){
        logger = Logger.getLogger( "myLogger" );
    }

    public static IotMonitor fromDevice(Device device){
        IotMonitor mon = null;
        if(device.getTypeId() == 1){ //Hue Light
            mon = new HueMonitor(device.getIp(), 80, device.getId(), device.getSamplingRate());
        }
        else if(device.getTypeId() == 2){ //Dlink Camera
            mon = new DLinkMonitor(device.getId());
        }
        else if (device.getTypeId() == 3){ //WeMo Insight
            mon = new WemoMonitor(device.getId(), device.getName(), device.getSamplingRate());
        }
        else if(device.getTypeId() == 4){ //Undoo Neo
            mon = new NeoMonitor(device.getId(), device.getIp(), device.getSamplingRate());
        }
        return mon;
    }
}
