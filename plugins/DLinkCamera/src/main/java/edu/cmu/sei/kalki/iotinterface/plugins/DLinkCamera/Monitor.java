package edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera;

import edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera.mail.EventObserver;
import edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera.mail.MailServer;
import edu.cmu.sei.kalki.iotinterface.common.device.IotMonitor;
import edu.cmu.sei.ttg.kalki.models.DeviceStatus;

public class Monitor extends IotMonitor implements EventObserver
{

    private static final String logId = "[DLinkCameraMonitor]";
    private static final String DLINK_EMAIL_DOMAIN = "@dlink.com";
    private static final int MAIL_PORT = 25000;

    private String deviceEmailSource = "";

    public Monitor(int deviceId, String ip, int samplingRate){
        super(deviceId, ip, false);
        deviceEmailSource = deviceId + DLINK_EMAIL_DOMAIN;
        MailServer.initialize(MAIL_PORT);
        MailServer.registerObserver(this);
        logger.info(logId + " Monitor started for device: "+deviceId);
    }

    /**
     * The camera is configured to send a notification from deviceEmailSource on motion detected.
     * This method sends a status to the DeviceController on an appropriate message
     * @param source The source the email was sent "from" from the camera
     */
    public void notify(String source){
        if (source.equals(deviceEmailSource)){
            DeviceStatus status = new DeviceStatus(deviceId);
            status.addAttribute("motion_detected", "true");
            sendToDeviceController(status);
        }
    }

}
