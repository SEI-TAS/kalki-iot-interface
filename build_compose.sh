#!/usr/bin/env bash
bash build_container.sh

(cd plugins/WeMoInsight/api && bash build_container.sh)
