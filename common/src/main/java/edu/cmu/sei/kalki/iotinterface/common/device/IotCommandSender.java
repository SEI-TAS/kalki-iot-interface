package edu.cmu.sei.kalki.iotinterface.common.device;

import edu.cmu.sei.kalki.iotinterface.common.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.StageLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

public abstract class IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String LOG_ID = "[IotCommandSender] ";

    protected Device device;
    private List<DeviceCommand> commands;

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
     * Method that uses reflection to call a method of the derived classes with the same name as the command that needs to be sent.
     */
    private void sendCommand(DeviceCommand command) {
        // Dashes are not valid in method names.
        String methodName = "command_" + command.getName().replace("-", "_");
        logger.info(LOG_ID + " Looking for method named " + methodName);

        // Get the method that implements this command. This will only work if used in derived classes.
        Method method = null;
        try {
            method = this.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            logger.warning("Method " + methodName + " not found.");
        }

        // Execute the actual method, and send a notification that it worked.
        if(method != null) {
            logger.info(LOG_ID + "Sending '" + command.getName() + "' command to device of type " + device.getType().getName() + ": " + device.getName() + "(id: " + device.getId() + ")");
            try {
                method.invoke(this);
                logSendCommand(command.getName());
            }
            catch (IllegalAccessException e) {
                logger.severe(LOG_ID + "Illegal access trying to access method: " + e.toString());
            }
            catch (InvocationTargetException e) {
                logger.severe(LOG_ID + "Invocation target error trying to access method: " + e.getCause().toString());
                e.getCause().printStackTrace();
            }
        }
        else {
            logger.severe(LOG_ID + "Command: " + command.getName() + " not supported for " + device.getType().getName());
        }
    }

    /**
     * Sends StageLog to Device Controller indicating a command was sent to the device
     * @param command The name of the command
     */
    private void logSendCommand(String command) {
        logger.info(LOG_ID + " Logging that a command was sent to the device.");
        if(device.getCurrentState() != null)
        {
            StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: " + command);
            DeviceControllerApi.sendLog(log);
        }
    }
}
