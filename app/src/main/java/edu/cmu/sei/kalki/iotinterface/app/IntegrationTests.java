package edu.cmu.sei.kalki.iotinterface.app;

import edu.cmu.sei.kalki.db.models.Device;
import edu.cmu.sei.kalki.db.models.DeviceCommand;
import edu.cmu.sei.kalki.db.models.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTests
{
    public static void runTest(String test, MonitorManager monitor) {
        if(test.equals("wemo_on"))
        {
            IntegrationTests.testWemoTurnOn();
        }
        else if(test.equals("wemo_off"))
        {
            IntegrationTests.testWemoTurnOff();
        }
        else if(test.equals("phle"))
        {
            IntegrationTests.testPHLETurnOn();
        }
        else if(test.equals("wemo_poll"))
        {
            IntegrationTests.testWemoPoll(monitor);
        }
    }

    private static void testWemoTurnOn() {
        testSendingCommand("WeMo Insight", "Wemo 1", "10.27.151.121", "turn-on");
    }

    private static void testWemoTurnOff() {
        testSendingCommand("WeMo Insight", "Wemo 1", "10.27.151.121", "turn-off");
    }

    private static void testPHLETurnOn() {
        testSendingCommand("Philips Hue Light Emulator", "PHLE 1", "10.27.151.106", "turn-on");
    }

    private static void testWemoPoll(MonitorManager monitorManager) {
        testMonitorStart("WeMo Insight", "Wemo 1", "10.27.151.121", monitorManager);
    }

    private static void testSendingCommand(String deviceTypeName, String deviceName, String deviceIp, String commandName) {
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

    private static void testMonitorStart(String deviceTypeName, String deviceName, String deviceIp, MonitorManager monitorManager) {
        DeviceType deviceType = new DeviceType();
        deviceType.setName(deviceTypeName);
        Device device = new Device();
        device.setId(1);
        device.setName(deviceName);
        device.setIp(deviceIp);
        device.setType(deviceType);
        device.setSamplingRate(1000);

        monitorManager.startMonitor(device);
    }

}
