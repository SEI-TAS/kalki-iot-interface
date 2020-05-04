# Kalki-IoT-Interface
Kalki component that monitors and polls information from IoT devices, as well as sends commands to them.

## Prerequisites
- Kalki-db library. See [here](https://github.com/SEI-TAS/kalki-db) for installation details.
- Docker has to be installed.
- Each API plugin may have additional setup requirements. Review the readme file for each plugin that is to be used to set up all necessary configs or dependencies.

## Configuration
The file app/config.json contains mandatory and optional configuration:

- device_controller_api_ip (mandatory): IP of the Control Node where the Device Controller is.
- device_controller_api_port (optional): port where Device Controller is listening, default should be ok in most cases.
- iot_interface_api_port (optional): port where IoT Interface is listening locally, default should be ok in most cases.

## Usage
### Kalki-Iot-Interface Only 
First compile and build a docker image with:

```
$ bash build_container.sh
```

To run:

```
$ bash run_container.sh
```

### Kalki-Iot-Interface and Device API plugins
To build and run along with any other containers that are needed for device type APIs.

To build:
```
$bash build_compose.sh
```

Or if behind a proxy:
```
$bash build_compose_proxy.sh
```

To run all:
```
$ bash run_compose.sh
```

### Test Mode
Optionally, the parameter ``test`` can be passed to either script above, to enter a simple test mode for  IoTInterface. Additional parameters have to be passed after this to execute specific tests. For more details, see the IotInterface.java source file.

## Adding New IoT Device API Plugins
To add an API implementation for a new device type, follow these steps:

1. Add a new project for the API to the repo: 
    1. Create a subfolder `/plugins/<DeviceTypeName>`, and add a `build.gradle` file that at least contains: `dependencies {compile project(':common')}`
    1. Create a `src/main/java` folder inside it, along with a package inside it called `edu.cmu.sei.kalki.iotinterface.plugins.<DeviceTypeName>`
    1. Modify `settings.gradle` in the root of the repo, and add the line `include ":plugins/<DeviceTypeName>`
    1. Modiffy `app/build.gradle` and add the dependency `compile project(':plugins/DeviceTypeName>')`
1. Implement either a Monitor or a CommandSender class, or both, for this device type:
    1. For Monitors:
        1. Create a class called `Monitor.java` inside the package created above, and inherit from `IotMonitor` or `PollingMonitor`
        1. Make sure the constructor calls the base constructor
        1. If deriving from `PollingMonitor`, override the `pollDevice()` method, and fill the `DeviceStatus` param that is received with the polled information
        1. If not deriving from `PollingMonitor`, override the `start()` and `stop()` methods as needed to initialize and stop your monitor. Be sure to manually call `sendStatusToDB()` when you have a new status to report.
    1. For Command Senders
        1. Create a class called `CommandSender.java` inside the package created above, and inherit from `IotCommandSender`
        1. Make sure the constructor calls the base constructor
        1. Create a method called `command_<command_name>()` for each command to be supported.
        