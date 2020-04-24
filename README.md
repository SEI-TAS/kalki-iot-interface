# Kalki-IoT-Interface
Kalki component that monitors and polls information from IoT devices, as well as sends commands to them.

## Requirements
- Kalki-db library. See [here](https://github.com/SEI-TAS/kalki-db) for installation details.
- Docker has to be installed.
- Each API plugin may have additional setup requirements. Review the readme file for each plugin that is to be used to set up all necessary configs or dependencies.

## To run: 
First compile and build a docker image with:

```
$ bash build_container.sh
```

To run:

```
$ bash run_container.sh
```

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
    1. For Command Senders
        1. Create a class called `CommandSender.java` inside the package created above, and inherit from `IotCommandSender`
        1. Make sure the constructor calls the base constructor
        1. Create a method called `command_<command_name>()` for each command to be supported.
        