# Kalki-IoT-Interface Plugin - DLink Camera
Kalki API Plugin for the Dlink Camera. Only contains a Monitor.

## Setup

### Requirements
- The Data Node must have the port 25000 available for the monitor to listen to push messsages on it.

### Device Setup

- The camera must first be connected to the Wi-Fi network via the "My dlink" phone app.
- The camera must also be configured via the web GUI to change the motion detection settings. 
- The sending email address must be configured via the web GUI so that the system can properly identify
events sent from each camera. The sending email must be <device-name>@dlink.com, where <device-name> has to be the
same name configured in the Control Node Dashboard for the device (removing all spaces if needed).

### Data Node Setup

None.

## API Implementation

Supported information commands:
- Motion detected (pushed by email from device).
