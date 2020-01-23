package edu.cmu.sei.kalki.devicetypes.DLinkCamera;

import edu.cmu.sei.kalki.utils.Config;
import edu.cmu.sei.kalki.utils.mail.EventObserver;
import edu.cmu.sei.kalki.utils.mail.MailServer;
import edu.cmu.sei.kalki.utils.IotMonitor;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

public class Monitor extends IotMonitor implements EventObserver {

    private final String listenEmail = Config.data.get("dlink_notification_email");

    public Monitor(int deviceId, String ip, int samplingRate){
        this.isPollable = false;
        this.pollInterval = samplingRate;
        this.deviceId = deviceId;
        MailServer.initialize();
        MailServer.registerObserver(this);
        logger.info("[Monitor] Monitor started for device: "+deviceId);
    }

    /**
     * The camera is configured to send a notification to listenEmail on motion detected.
     * This method sends a status to the DeviceController on an appropriate message
     * @param message The notification from the camera
     */
    public void notify(String message){
        if (message.equals(listenEmail)){
            DeviceStatus status = new DeviceStatus(deviceId);
            status.addAttribute("motion_detected", "true");
            sendToDeviceController(status);
        }
    }

}
