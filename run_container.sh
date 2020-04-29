#!/usr/bin/env bash
export http_proxy=""
export https_proxy=""
export HTTP_PROXY=""
export HTTPS_PROXY=""

HOST_TZ=$(cat /etc/timezone)
docker run --rm -e TZ=${HOST_TZ} \
           --network=host \
           --name kalki-iot-interface kalki/kalki-iot-interface "@"
