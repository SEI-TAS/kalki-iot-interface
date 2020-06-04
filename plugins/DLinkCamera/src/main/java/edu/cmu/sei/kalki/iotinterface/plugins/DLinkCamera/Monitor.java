package edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera;

import edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera.mail.EventObserver;
import edu.cmu.sei.kalki.iotinterface.plugins.DLinkCamera.mail.MailServer;
import edu.cmu.sei.kalki.iotinterface.common.device.IotMonitor;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

public class Monitor extends IotMonitor implements EventObserver
{
    private static final String LOG_ID = "[DLinkCameraMonitor]";
    private static final String DLINK_EMAIL_DOMAIN = "@dlink.com";
    private static final int MAIL_PORT = 25000;

    private String deviceEmailSource = "";

    public Monitor(Device device){
        super(device);

        String sanitizedDeviceName = device.getName().replace(" ", "");
        deviceEmailSource = sanitizedDeviceName + DLINK_EMAIL_DOMAIN;

        MailServer.initialize(MAIL_PORT);
        MailServer.registerObserver(this);
    }

    public void start() {
        logger.info(LOG_ID + " Monitor started for device: " + device.getName());
        MailServer.start();
    }

    public void stop() {
        logger.info(LOG_ID + " Monitor stopped for device: " + device.getName());
        MailServer.stop();
    }

    /**
     * The camera is configured to send a notification from deviceEmailSource on motion detected.
     * This method sends a status to the DeviceController on an appropriate message
     * @param source The source the email was sent "from" from the camera
     */
    public void notify(String source){
        if (source.equals(deviceEmailSource)){
            DeviceStatus status = new DeviceStatus(device.getId());
            status.addAttribute("motion_detected", "true");
            sendToDeviceController(status);
        }
    }

}
