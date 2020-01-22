package edu.cmu.sei.kalki;

import edu.cmu.sei.kalki.utils.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

public class CommandManager {
    private static Logger logger = Logger.getLogger("iot-interface");

    public CommandManager(){}

    /**
     * Creates command sender from device's type and sends the list of commands
     * @param device The device receiving commands
     * @param commands The list of commands to send
     * @param apiUrl The url of the DeviceControllerApi
     */
    public static void processCommands(Device device, List<DeviceCommand> commands, String apiUrl){
        try {
            // Remove white spaces from device type name
            String deviceTypeName = device.getType().getName();
            deviceTypeName.replace("\\s+","");

            // Get command sender constructor via reflection
            String classPath = "edu.cmu.kalki.devicetypes."+deviceTypeName+".CommandSender";
            Constructor con = Class.forName(classPath).getConstructor(Device.class, List.class, String.class);

            // Create instance and send sommands
            IotCommandSender commandSender = (IotCommandSender) con.newInstance(device, commands, apiUrl);
            commandSender.sendCommands();
        } catch (Exception e) { // No command sender found for the given device type
            logger.severe("[CommandManager] Error: there are no commands for a "+device.getType().getName());
        }
    }
}
