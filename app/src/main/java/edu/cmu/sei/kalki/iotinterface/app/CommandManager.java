package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.kalki.iotinterface.common.device.IotCommandSender;
import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

public class CommandManager {
    private static Logger logger = Logger.getLogger("iot-interface");
    private final static String LOG_ID = "[CommandManager]";

    public CommandManager(){}

    /**
     * Creates command sender from device's type and sends the list of commands
     * @param device The device receiving commands
     * @param commands The list of commands to send
     */
    public static void processCommands(Device device, List<DeviceCommand> commands){
        try {
            // Remove white spaces from device type name
            String deviceTypeName = device.getType().getName().replaceAll("\\s+","");

            // Get command sender constructor via reflection
            String classPath = "edu.cmu.sei.kalki.iotinterface.plugins." + deviceTypeName + ".CommandSender";
            logger.info(LOG_ID + "ComandSender class to load: " + classPath);
            Constructor con = Class.forName(classPath).getConstructor(Device.class, List.class);

            // Create instance and send commands
            IotCommandSender commandSender = (IotCommandSender) con.newInstance(device, commands);
            commandSender.sendCommands();
        } catch (Exception e) { // No command sender found for the given device type
            logger.severe(LOG_ID + "Error: error executing commands for device type " + device.getType().getName());
            logger.severe(LOG_ID + "Error: "+ e);
            e.printStackTrace();
        }
    }
}
