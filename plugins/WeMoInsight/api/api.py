import sys

from flask import Flask
from flask_restful import Api, Resource

import wemo

# API info.
API_BASE_URL = "/plugins/wemo"
API_PORT = "7501"

# Reply keys and values.
STATUS_KEY = "status"
OK_VALUE = "ok"
ERROR_VALUE = "error"
ERROR_DETAILS_KEY = "error"


class WemoScript(Resource):
    """Resource for handling Wemo commands."""

    def post(self, ip_address, command):
        try:
            result = wemo.send_command(ip_address, command)
            return {STATUS_KEY: result}
        except Exception as e:
            error_msg = "Error executing wemo comand: " + str(e)
            print(error_msg)
            sys.stdout.flush()
            return {STATUS_KEY: ERROR_VALUE, ERROR_DETAILS_KEY: error_msg}


def main():
    print("Loading wemo API server")
    sys.stdout.flush()

    app = Flask(__name__)
    api = Api(app)
    api.add_resource(WemoScript, API_BASE_URL + "/<string:ip_address>/<string:command>")
    app.run(host="127.0.0.1", port=API_PORT, debug=True)


if __name__ == "__main__":
    main()
