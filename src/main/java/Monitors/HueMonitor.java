package Monitors;

import edu.cmu.sei.ttg.kalki.models.*;
import edu.cmu.sei.ttg.kalki.database.Postgres;

import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// Rulebook imports
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

//Adapted from https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android/tree/master/JavaDesktopApp

public class HueMonitor extends PollingMonitor {

    private PHHueSDK phHueSDK;
    private String username;
    private String ip;
    private static final int MAX_HUE=65535;

    private List<DeviceStatus> lights = new ArrayList<DeviceStatus>();
    private DeviceStatus status;

    public HueMonitor(String ip, int port, int deviceId, int samplingRate) {
        super();
        logger.info("Starting Hue monitor");

        this.ip = ip + ":" + port;

        PHHueSDK phHueSDK = PHHueSDK.create();

        phHueSDK.getNotificationManager().registerSDKListener(listener);
        this.pollInterval = samplingRate;
        this.phHueSDK = PHHueSDK.getInstance();
        this.deviceId = deviceId;
        this.username = "f450ab20effc384c3298bbcf745272a";
        start();
    }

    @Override
    public void start(){
        connectToDevice();
        super.start();
    }

    public void findBridges() {
        phHueSDK = PHHueSDK.getInstance();
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
            logger.info("Found AccessPoints!");

        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            // Start the Pushlink Authentication.
            logger.info("AuthenticationRequired");
            phHueSDK.startPushlinkAuthentication(accessPoint);
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            logger.info("Connected to bridge");
            String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            logger.info("IP is : " + lastIpAddress);
            logger.info("Username is: " + username);
            if(pollingEnabled){
                phHueSDK.disableAllHeartbeat();
                phHueSDK.enableHeartbeat(bridge, getHeartbeatInterval());
            }
            randomLights();
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {

            if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                logger.info("Bridge not responding: " + message);
            }
            else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                logger.info("Button not pressed: " + message);
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                    logger.info("Authentication failed: " + message);
            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                logger.info("Bridge not found: " + message);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {  
            for (PHHueParsingError parsingError: parsingErrorsList) {
                logger.info("ParsingError : " + parsingError.getMessage());
            }
        } 
    };

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        PHBridgeResourcesCache cache = bridge.getResourceCache();

        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
        }
    }

    /**
     * Connects to bridge using the given ip.
     * @param ip address of the bridge.
     * @return true if there was sufficient information to attempt a connection.
     */
    public boolean connectToAccessPoint(String ip) {
        if (username==null || ip == null) {
            logger.info("Missing Last Username or Last IP.  Last known connection not found.");
            return false;
        }
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(ip);
        accessPoint.setUsername(username);
        phHueSDK.connect(accessPoint);
        return true;
    }

    @Override
    public void startPolling(){
        super.startPolling();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null){
            phHueSDK.enableHeartbeat(bridge, getHeartbeatInterval());
        }
    }

    /**
     * Heartbeat interval needs to be slightly less than the pollInterval to guarantee correctness.
     * @return
     */
    private int getHeartbeatInterval(){
        return Math.max(pollInterval/3, pollInterval-300);
    }

    @Override
    public void stopPolling() {
        super.stopPolling();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        phHueSDK.disableHeartbeat(bridge);
    }

    @Override
    public void pollDevice() {
        PHHueSDK phHueSDK = PHHueSDK.getInstance();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge == null){
            logger.severe("Null Bridge");
            return;
        }
        PHBridgeResourcesCache cache = bridge.getResourceCache();
        // And now you can get any resource you want, for example:
        List<PHLight> myLights = cache.getAllLights();
        lights = new ArrayList<DeviceStatus>();
        for(PHLight light : myLights){
            PHLightState state = light.getLastKnownLightState();
            String id = light.getUniqueId();
            if (id == null){
                id = UUID.randomUUID().toString();
                light.setUniqueId(id);
            }
            DeviceStatus newLight = new DeviceStatus(deviceId);
            newLight.addAttribute("brightness", state.getBrightness().toString());
            newLight.addAttribute("hue", state.getHue().toString());
            newLight.addAttribute("isOn", state.isOn().toString());
            newLight.addAttribute("lightId", id);
            lights.add(newLight);
        }
    }

    @Override
    public void saveCurrentState() {
        for(DeviceStatus light : lights){
            light.insert();
        }
    }

    @Override
    public void runAlertRules() {
        Device device = Postgres.findDevice(deviceId);

        NameValueReferableMap facts = new FactMap();
        facts.setValue("device", device);
        facts.setValue("status", status);

        RuleBookRunner ruleBook = new RuleBookRunner("Rulebooks.unts");
        ruleBook.run(facts);
    }

    public void connectToDevice() {
        connectToAccessPoint(ip);
    }
}
