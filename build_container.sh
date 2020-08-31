#!/usr/bin/env bash

SKIP_TESTS_ARG=""
if [ "$1" == "--skip_tests" ]; then
  SKIP_TESTS_ARG=" -x test "
fi

docker build --build-arg SKIP_TESTS="${SKIP_TESTS_ARG}" -t kalki/kalki-iot-interface .

(cd plugins/WeMoInsight/api && bash build_container.sh)
