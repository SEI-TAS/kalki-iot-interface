from ouimeaux.environment import Environment
from ouimeaux.environment import UnknownDevice
from ouimeaux.device.insight import Insight
import time
import sys


def send_command(insight_ip, command):
    try:
        device = Insight("http://"+insight_ip+":49153/setup.xml")
        if command == "turn-off":
            device.off()
            return "Insight turned off: " + device.name
        elif command == "turn-on":
            device.on()
            return "Insight turned on: " + device.name
        elif command == "status":
            result = device.insight_params
            result['today_kwh'] = device.today_kwh
            result['today_standby_time'] = device.today_standby_time
            result['lastchange'] = result['lastchange'].strftime("%Y-%m-%d %H:%M:%S")
            if result['state'] == '1':
                result['isOn'] = True
            else:
                result['isOn'] = False
            return result
        else:
            return "Unknown command"
    except Exception as e:
        print("Exception happened " + str(e))


if __name__ == "__main__":
    insight_ip = sys.argv[1]
    command = sys.argv[2]
    result = send_command(insight_ip, command)
    print(result)
