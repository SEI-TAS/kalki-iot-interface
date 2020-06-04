package edu.cmu.sei.kalki.iotinterface.plugins.AmcrestIP8M2496ECamera;

import edu.cmu.sei.kalki.iotinterface.common.device.PollingMonitor;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class Monitor extends PollingMonitor {
    private static final String logId = "[AmcrestMonitor]";

    public Monitor(Device device) {
        super(device);
    }

    /**
     * Obtains information for the Amcrest camera
     * @param status The DeviceStatus to be sent to the DeviceControllerApi
     */
    @Override
    public void pollDevice(DeviceStatus status) {
        try {

        }
        catch(Exception e) {
            logger.severe(logId + " Exception happened - here's what I know: ");
            logger.severe(e.getMessage());
        }
    }
}