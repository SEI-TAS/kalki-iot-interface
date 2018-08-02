from ouimeaux.environment import Environment
from ouimeaux.environment import UnknownDevice
from ouimeaux.device.insight import Insight
import time
import sys

insight_name = "WeMo Insight"
insight_name = sys.argv[1]

def on_switch(switch):
    return

env = Environment(on_switch)

env.start()
env.discover(seconds=3)

try:
    switch = env.get_switch(insight_name)

    result = switch.insight_params
    result['today_kwh'] = switch.today_kwh
    result['current_power'] = switch.current_power
    result['today_on_time'] = switch.today_on_time
    result['today_standby_time'] = switch.today_standby_time
    result['lastchange'] = result['lastchange'].strftime("%Y-%m-%d %H:%M:%S")
    print(result)

except UnknownDevice:
    print("Unknown Device")
