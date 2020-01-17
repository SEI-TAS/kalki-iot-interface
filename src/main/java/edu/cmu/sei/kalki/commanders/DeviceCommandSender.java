package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.kalki.utils.DeviceControllerApi;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;
import edu.cmu.sei.ttg.kalki.models.StageLog;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

public abstract class DeviceCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");

    protected Device device;
    protected List<DeviceCommand> commands;
    protected String apiUrl;


    public DeviceCommandSender(){}

    public DeviceCommandSender(Device device, List<DeviceCommand> commands, String apiUrl){
        this.device = device;
        this.commands = commands;
        this.apiUrl = apiUrl;
    }

    public static void processCommands(Device device, List<DeviceCommand> commands, String apiUrl){
        try {
            String classPath = "edu.cmu.kalki.commanders."+getDeviceTypeCommandSenderClassName(device.getType().getName());
            Constructor con = Class.forName(classPath).getConstructor(Device.class, List.class, String.class);
            DeviceCommandSender commandSender = (DeviceCommandSender) con.newInstance(device, commands, apiUrl);
            commandSender.sendCommands();
        } catch (Exception e) {
            logger.severe("[DeviceCommandSender] Error: there are no commands for a "+device.getType().getName());
        }
    }

    /**
     * Removes spaces from device type's name and append 'Monitor'
     * @param devTypeName
     * @return device type's monitor class name
     */
    private static String getDeviceTypeCommandSenderClassName(String devTypeName) {
        String[] temp = devTypeName.split(" ");
        String name = "";
        for(int i=0;i<temp.length;i++){
            name+=temp[i];
        }
        name+="CommandSender";
        return name;
    }

    /**
     * Sends StageLog to Device Controller indicating a command was sent to the device
     * @param command The name of the command
     */
    protected void logSendCommand(String command) {
        logger.info("[DeviceCommandSender] Logging that a command was sent to the device.");
        StageLog log = new StageLog(device.getCurrentState().getId(), StageLog.Action.SEND_COMMAND, StageLog.Stage.FINISH, "Sent command to device: "+command);
        DeviceControllerApi.sendLog(log, apiUrl);
    }

    public abstract void sendCommands();

}
