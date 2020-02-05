package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTests
{
    public static void commandSenderTests() {
        // Test
        DeviceType deviceType = new DeviceType();
        //deviceType.setName("WeMo Insight");
        deviceType.setName("Philips Hue Light Emulator");
        Device device = new Device();
        device.setId(1);
        device.setIp("127.0.0.1");
        device.setType(deviceType);
        List<DeviceCommand> commands = new ArrayList<>();
        DeviceCommand command = new DeviceCommand();
        command.setName("turn-on");
        commands.add(command);

        CommandManager.processCommands(device, commands);
    }
}
