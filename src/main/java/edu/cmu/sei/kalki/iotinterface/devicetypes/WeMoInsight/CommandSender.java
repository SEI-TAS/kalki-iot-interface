package edu.cmu.sei.kalki.iotinterface.devicetypes.WeMoInsight;

import edu.cmu.sei.kalki.iotinterface.devicetypes.IotCommandSender;
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

    @Override
    protected void sendCommand(DeviceCommand command) {
        switch (command.getName()){
            case "turn-on":
            case "turn-off":
                WemoScript.executeScript(command.getName(), device.getIp());
                logSendCommand(command.getName());
                break;
            default:
                logger.severe(logId + " Command: " + command.getName() + " is not a valid command for a Wemo Insight");
                return;
        }
    }



}
