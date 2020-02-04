package edu.cmu.sei.kalki.iotinterface.plugins.PhilipsHueLightEmulator;

import edu.cmu.sei.kalki.iotinterface.common.device.IotCommandSender;
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
    }

    /**
     * Implements a command called "turn-on" (or "turn_on").
     */
    protected void command_turn_on() {
        turnOnOrOff(true);
    }

    /**
     * Implements a command called "turn-off" (or "turn_off").
     */
    protected void command_turn_off() {
        turnOnOrOff(false);
    }

    /**
     * Handles both turn on or off commands.
     * @param turnOn true if we want to turn on, false otherwise.
     */
    private void turnOnOrOff(boolean turnOn) {
        if(lights == null){
            lights = PHLEApi.getAllLights(device.getIp(), authCode);
            if(lights == null) {
                logger.severe(logId + " Unable to get lights from bridge");
                return;
            }
        }

        String turnOnString = turnOn ? "true" : "false";
        Iterator<String> lightIds = lights.keys();
        while(lightIds.hasNext()) {
            int id = Integer.parseInt(lightIds.next());
            logger.info(logId + " Sending command to light id: " + id);
            PHLEApi.sendIsOn(device.getIp(), authCode, id, turnOnString);
        }
    }
}
