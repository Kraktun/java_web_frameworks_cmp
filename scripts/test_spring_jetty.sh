#!/bin/bash

source set_main_dir.sh

SERVICE_NAME='spring_jetty'
APP_PATH="$MAIN_DIR/runs/springrest_jetty.jar"
YOUR_COMMAND="java -jar ${APP_PATH}"

# note: don't source sdkman or it restores the default jdk
JAVA_VER=$(java -version 2>&1)
echo "Java version:"
echo $JAVA_VER

chmod u+x run_test.sh
./run_test.sh $SERVICE_NAME "$APP_PATH" "$YOUR_COMMAND"
