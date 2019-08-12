package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

import java.util.List;

public class DeviceCommandSender {

    public static void sendCommands(Device device, List<DeviceCommand> commands){
        DeviceType deviceType = device.getType();
        switch (deviceType.getId()){
            case 1: // DLC
                System.out.println("Error: there are no commands for a DLink Camera");
                break;
            case 2: // UNTS
                System.out.println("Error: there are no commands for a Udoo Neo with Temperature Sensor");
                break;
            case 3: //WeMo
                WemoCommandSender.sendCommands(device, commands);
                break;
            case 4: // PHLE
                PhleCommandSender.sendCommands(device, commands);
                break;
            default:
                System.out.println("System not configured to send commands to type: " + deviceType.getName());
        }
    }
}
