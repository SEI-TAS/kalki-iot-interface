package Monitors;

import Mail.MailServer;
import kalkidb.models.DeviceHistory;

public class DLinkMonitor extends IotMonitor implements EventObserver {

    private String listenEmail = "camera1@dlink.com";

    public DLinkMonitor(int deviceId){
        MailServer.initialize();
        MailServer.registerObserver(this);
    }
    public void notify(String message){
        if (message.equals(listenEmail)){
            DeviceHistory history = new DeviceHistory(deviceId);
            history.addAttribute("motion_detected", "true");
            history.insert();
        }
    }

    public void saveCurrentState(){

    }

    public void pollDevice(){

    }
}
