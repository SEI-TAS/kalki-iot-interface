#!/usr/bin/env bash
HOST_TZ=$(cat /etc/timezone)
docker run --rm -e TZ=${HOST_TZ} \
           --network=host \
           --name kalki-iot-interface kalki/kalki-iot-interface "@"
