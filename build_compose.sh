#!/usr/bin/env bash
./gradlew build
docker-compose build --build-arg HTTP_PROXY=http://proxy.sei.cmu.edu:8080 \
                     --build-arg HTTPS_PROXY=http://proxy.sei.cmu.edu:8080 \
                     --build-arg http_proxy=http://proxy.sei.cmu.edu:8080 \
                     --build-arg https_proxy=http://proxy.sei.cmu.edu:8080
