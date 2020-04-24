#!/usr/bin/env bash
./gradlew build
docker build -t kalki/kalki-iot-interface .
