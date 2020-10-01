#!/usr/bin/env bash

SKIP_TESTS_ARG=""
if [ "$1" == "--skip_tests" ]; then
  SKIP_TESTS_ARG=" -x test "
fi

KALKI_DB_VER="1.7.0"
docker build --build-arg SKIP_TESTS="${SKIP_TESTS_ARG}" --build-arg KALKI_DB_VER="${KALKI_DB_VER}" -t kalki/kalki-iot-interface .

(cd plugins/WeMoInsight/api && bash build_container.sh)
