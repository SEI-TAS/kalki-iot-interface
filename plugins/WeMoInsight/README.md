# Kalki-IoT-Interface Plugin - WeMo Insight
Kalki API Plugin for the WeMo Insight. Contains a Monitor and a Command Sender.

## Setup

### Requirements
None.

### Device Setup

- The device must first be connected to Wi-Fi via the WeMo phone app.

### Data Node Setup

- Docker has to be installed on the node that has access to the WeMo device.
- Create the Docker container that will have an HTTP API that communicates with the WeMo device:
```
$ bash build_container.sh
```
- To run the container with the HTTP API that will communicate with the WeMo device:
```
$ bash run_container.sh
```
- Once the container is running, the WeMo plugin will be able to communicate with the WeMo device through it.

## API Implementation

Supported action commands:
- turn-on
- turn-off

Supported information commands:
- status
