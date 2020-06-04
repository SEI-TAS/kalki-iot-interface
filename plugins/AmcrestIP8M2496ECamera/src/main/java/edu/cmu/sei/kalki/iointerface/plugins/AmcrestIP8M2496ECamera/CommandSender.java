package edu.cmu.sei.kalki.iotinterface.plugins.AmcrestIP8M2496ECamera;

import edu.cmu.sei.kalki.iotinterface.common.device.IotCommandSender;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[PhleCommandSender]";

    public CommandSender(Device device, List<DeviceCommand> commands) {
        super(device, commands);
    }

    protected void command_reboot() {
        
    }

    protected void command_shutdown() {
        
    }
}