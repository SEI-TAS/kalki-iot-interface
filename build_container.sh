#!/usr/bin/env bash
docker build -t kalki/kalki-iot-interface .

(cd plugins/WeMoInsight/api && bash build_container.sh)
