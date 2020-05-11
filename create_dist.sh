#!/usr/bin/env bash

DIST_PATH=$1
if [ -z "${DIST_PATH}" ]; then
  echo "Destination dist path argument required"
  exit 1
fi

# Needed as workaround to docker-compose bug in production.
mkdir -p ${DIST_PATH}/plugins/WeMoInsight/api
