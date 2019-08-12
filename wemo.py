from ouimeaux.environment import Environment
from ouimeaux.environment import UnknownDevice
from ouimeaux.device.insight import Insight
import time
import sys

#insight_name = "Kalki"
insight_name = sys.argv[1]
command = sys.argv[2]

def on_switch(switch):
    return

def on_motion(motion):
    return

env = Environment(on_switch, on_motion)

env.start()
env.discover(seconds=2)

try:
    switch = env.get_switch(insight_name)

    if command == "turn-off":
        switch.off()
        print "Insight turned off:", switch.name
    if command == "turn-on":
        switch.on()
        print "Insight turned on:", switch.name
    if command == "status":
        result = switch.insight_params
        result['today_kwh'] = switch.today_kwh
        result['today_standby_time'] = switch.today_standby_time
        result['lastchange'] = result['lastchange'].strftime("%Y-%m-%d %H:%M:%S")
        print(result)

except UnknownDevice:
    print("Unknown Device")
