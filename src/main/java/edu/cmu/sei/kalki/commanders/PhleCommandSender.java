package edu.cmu.sei.kalki.commanders;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;

import java.util.List;

public class PhleCommandSender {

    public static void sendCommands(Device device, List<DeviceCommand> commands){
        for(DeviceCommand command: commands){
            switch (command.getName()){
                case "turn-on":
                    System.out.println("Sending 'turn-on' command to PHLE: " + device.getId());
                    break;
                case "turn-off":
                    System.out.println("Sending 'turn-off' command to PHLE: " + device.getId());
                    break;
                case "set-name":
                    System.out.println("Sending 'set-name' command to PHLE: " + device.getId());
                    break;
                case "set-brightness":
                    System.out.println("Sending 'set-brightness' command to PHLE: " + device.getId());
                    break;
                case "set-color":
                    System.out.println("Sending 'set-color' command to PHLE: " + device.getId());
                    break;
                case "set-schedule":
                    System.out.println("Sending 'set-schedule' command to PHLE: " + device.getId());
                    break;
                case "set-group":
                    System.out.println("Sending 'set-group' command to PHLE: " + device.getId());
                    break;
                case "set-scene":
                    System.out.println("Sending 'set-scene' command to PHLE: " + device.getId());
                    break;
                default:
                    System.out.println("Command: " + command.getName() + " not supported for Phillips Hue Light Emulator.");
            }
        }
    }
}
