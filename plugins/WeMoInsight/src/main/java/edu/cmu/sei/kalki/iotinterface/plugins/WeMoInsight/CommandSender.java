package edu.cmu.sei.kalki.iotinterface.plugins.WeMoInsight;

import edu.cmu.sei.kalki.iotinterface.common.device.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import java.util.logging.Logger;
import java.util.List;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[WemoCommandSender]";

    public CommandSender(Device device, List<DeviceCommand> commands) {
        super(device, commands);
    }

    /**
     * Implements a command called "turn-on" (or "turn_on").
     */
    protected void command_turn_on() {
        WemoScript.executeScript("turn-on", device.getIp());
    }

    /**
     * Implements a command called "turn-off" (or "turn_off").
     */
    protected void command_turn_off() {
        WemoScript.executeScript("turn-off", device.getIp());
    }
}
