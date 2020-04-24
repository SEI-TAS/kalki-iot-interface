# Kalki-IoT-Interface Plugin - WeMo Insight
Kalki API Plugin for the WeMo Insight. Contains a Monitor and a Command Sender.

## Setup

### Requirements
None.

### Device Setup

- The device must first be connected to Wi-Fi via the WeMo phone app.

### Data Node Setup

- Python, PIP and Pipenv must be installed to set up the dependencies needed for the Wemo Python script. This can be easily done by running from this folder:
```
$ bash pipenv_setup.sh
```
- This will create a folder called temp/wemo on the root of the IoT-Inteface folder, where the script and its dependencies will be set up (and automatically called later).

## API Implementation

Supported action commands:
- turn-on
- turn-off

Supported information commands:
- status
