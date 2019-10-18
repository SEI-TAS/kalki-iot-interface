package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

import java.util.List;
import java.util.logging.Logger;

public abstract class DeviceCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");

    public static void sendCommands(Device device, List<DeviceCommand> commands, String apiUrl){
        DeviceType deviceType = device.getType();
        switch (deviceType.getId()){
            case 1: // DLC
                logger.severe("[DeviceCommandSender] Error: there are no commands for a DLink Camera");
                break;
            case 2: // UNTS
                logger.severe("[DeviceCommandSender] Error: there are no commands for a Udoo Neo with Temperature Sensor");
                break;
            case 3: //WeMo
                WemoCommandSender.sendCommands(device, commands, apiUrl);
                break;
            case 4: // PHLE
                PhleCommandSender.sendCommands(device, commands, apiUrl);
                break;
            default:
                logger.severe("[DeviceCommandSender] System not configured to send commands to type: " + deviceType.getName());
        }
    }

}
