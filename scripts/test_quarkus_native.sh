#!/bin/bash

source set_main_dir.sh

SERVICE_NAME='quarkus_native'
APP_PATH="$MAIN_DIR/runs/quarkusrest_native"
YOUR_COMMAND="${APP_PATH}"

chmod u+x run_test.sh
./run_test.sh $SERVICE_NAME "$APP_PATH" "$YOUR_COMMAND"
