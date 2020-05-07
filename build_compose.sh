#!/usr/bin/env bash
./gradlew build
docker-compose build --build-arg HTTP_PROXY=${HTTP_PROXY} \
                     --build-arg HTTPS_PROXY=${HTTPS_PROXY} \
                     --build-arg http_proxy=${http_proxy} \
                     --build-arg https_proxy=${https_proxy} \
                     kalki-iot-interface

docker-compose build --build-arg HTTP_PROXY=${HTTP_PROXY} \
                     --build-arg HTTPS_PROXY=${HTTPS_PROXY} \
                     --build-arg http_proxy=${http_proxy} \
                     --build-arg https_proxy=${https_proxy} \
                     kalki-iot-wemo
