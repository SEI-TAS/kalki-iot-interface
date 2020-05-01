#!/usr/bin/env bash
CMD_PARAMS="$@"
HOST_TZ=$(cat /etc/timezone)
docker-compose up
