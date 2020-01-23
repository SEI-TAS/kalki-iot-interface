package edu.cmu.sei.kalki.iotinterface.devicetypes;

import edu.cmu.sei.kalki.iotinterface.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.StageLog;

import java.util.List;
import java.util.logging.Logger;

public abstract class IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");

    protected Device device;
    protected List<DeviceCommand> commands;


    public IotCommandSender(Device device, List<DeviceCommand> commands){
        this.device = device;
        this.commands = commands;
    }

    public void sendCommands() {
        for(DeviceCommand command: commands) {
            sendCommand(command);
        }
    }

    /**
     * Method to be overwritten by child class. Check if it's a valid command, then send accordingly
     */
    protected abstract void sendCommand(DeviceCommand command);

    /**
     * Sends StageLog to Device Controller indicating a command was sent to the device
     * @param command The name of the command
     */
    protected void logSendCommand(String command) {
        logger.info("[IotCommandSender] Logging that a command was sent to the device.");
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: "+command);
        DeviceControllerApi.sendLog(log);
    }
}
