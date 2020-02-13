package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import edu.cmu.sei.ttg.kalki.models.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTests
{
    public static void testWemoTurnOn () {
        commandSenderTests("WeMo Insight", "Wemo 1", "10.27.151.121", "turn-on");
    }

    public static void testPHLETurnOn () {
        commandSenderTests("Philips Hue Light Emulator", "PHLE 1", "10.27.151.106", "turn-on");
    }

    public static void commandSenderTests(String deviceTypeName, String deviceName, String deviceIp, String commandName) {
        DeviceType deviceType = new DeviceType();
        deviceType.setName(deviceTypeName);
        Device device = new Device();
        device.setId(1);
        device.setName(deviceName);
        device.setIp(deviceIp);
        device.setType(deviceType);
        List<DeviceCommand> commands = new ArrayList<>();
        DeviceCommand command = new DeviceCommand();
        command.setName(commandName);
        commands.add(command);

        CommandManager.processCommands(device, commands);
    }
}
