# iot-monitor
Kalki controller component that monitors and polls information from IoT devices


####DLink Camera
Camera must first be connected to the wifi network via the my dlink phone app.

The camera must also be configured via the web GUI to change the motion detection settings. Cameras are identified by the monitor by the sending email address specified in these settings.


####WeMo Insight
WeMo must first be connected to wifi via the WeMo phone app.
The WeMo is discovered by its name, which can be set in the app and must be specified when adding the device to be monitored.

###Udoo Neo
The Udoo Neo is monitored through ssh connections, so your machine's ssh keys must first be put on the Udoo Neo, or authentication settings must otherwise be altered to allow for easy communication with the Udoo Neo.

###Hue Bridge
To use the Hue Bridge emulator, you must first get a username / token that you will use when sending requests to the bridge.

The command to get the username token is found at scripts/getHueUser.bash. 

With a physical bridge, you will need to press the button on the bridge before sending this request.