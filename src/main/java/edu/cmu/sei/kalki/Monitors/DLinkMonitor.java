package edu.cmu.sei.kalki.Monitors;

import edu.cmu.sei.kalki.Mail.EventObserver;
import edu.cmu.sei.kalki.Mail.MailServer;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

public class DLinkMonitor extends IotMonitor implements EventObserver {

    private String listenEmail = "camera1@dlink.com";

    public DLinkMonitor(int deviceId){
        MailServer.initialize();
        MailServer.registerObserver(this);
        isPollable = false;
        logger.info("[DLinkMonitor] Monitor started for device: "+deviceId);
        this.deviceId = deviceId;
    }
    public void notify(String message){
        if (message.equals(listenEmail)){
            DeviceStatus status = new DeviceStatus(deviceId);
            status.addAttribute("motion_detected", "true");
            sendToDeviceController(status);
        }
    }

}
