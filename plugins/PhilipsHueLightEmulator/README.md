# Kalki-IoT-Interface Plugin - Phillips Hue Light Emulator
Kalki API Plugin for the Phillips Hue Light Emulator. Contains a Monitor and a Command Sender.

## Setup

### Requirements
None.

### Device Setup

- The PHLE must have at least one light set up.
- The "newdeveloper" user/auth code must be enabled.

### Data Node Setup

None.

## Notes
To set up a different user, you must first get a username / token that you will use when sending requests to the bridge.

The command to get the username token is found at scripts/getHueUser.bash. 

With a physical bridge, you will need to press the button on the bridge before sending this request.

## API Implementation

Supported action commands:
- Set on a specific light
- Set off a specific light

Supported information commands:
- Lights status

