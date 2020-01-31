package edu.cmu.sei.kalki.iotinterface.devicetypes.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.iotinterface.devicetypes.IotCommandSender;
import edu.cmu.sei.ttg.kalki.models.Device;
import edu.cmu.sei.ttg.kalki.models.DeviceCommand;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class CommandSender extends IotCommandSender {
    private static Logger logger = Logger.getLogger("iot-interface");
    private static final String logId = "[PhleCommandSender]";

    // TODO: This should be part of the device information.
    private String authCode = "newdeveloper"; //Default username works for most GET operations

    private JSONObject lights;

    public CommandSender(Device device, List<DeviceCommand> commands) {
        super(device, commands);
        lights = PHLEApi.getAllLights(device.getIp(), authCode);
    }

    /**
     * Sends commands to each light associated with the given PHLE bridge
     * @param command Currently supports turn-on & turn-off
     */
    @Override
    protected void sendCommand(DeviceCommand command) {
        logger.info(logId + " Sending commands to PHLE: " + device.getId());

        if(lights== null){
            logger.severe(logId + " Unable to get lights from bridge");
            return;
        }
        Iterator<String> lightIds = lights.keys();
        while(lightIds.hasNext()){
            int id = Integer.parseInt(lightIds.next());
            switch (command.getName()){
                case "turn-on":
                    logger.info(logId + " Sending 'turn-on' command to PHLE: " + device.getId());
                    PHLEApi.sendIsOn(device.getIp(), authCode, id,"true");
                    logSendCommand(command.getName());
                    break;
                case "turn-off":
                    logger.info(logId + " Sending 'turn-off' command to PHLE: " + device.getId());
                    PHLEApi.sendIsOn(device.getIp(), authCode, id,"false");
                    logSendCommand(command.getName());
                    break;
                case "set-name":
                case "set-brightness":
                case "set-color":
                case "set-schedule":
                case "set-group":
                case "set-scene":
                default:
                    logger.severe(logId + " Command: " + command.getName() + " not supported for Phillips Hue Light Emulator.");
            }
        }
    }

}
