#!/usr/bin/env bash
HOST_TZ=$(cat /etc/timezone)
docker run --rm -e TZ=${HOST_TZ} \
           --network=host \
           --name kalki-iot-wemo kalki/kalki-iot-wemo "$@"
