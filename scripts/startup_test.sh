#!/bin/bash

source set_main_dir.sh

PROCESS_PID_FILE="temp.pid"
rm -f $PROCESS_PID_FILE
mkdir -p log

case $1 in
    quarkus)
        echo "Testing Quarkus startup time..."
        echo "Start:" `(date +"%T.%3N")` >> "log/quarkus_startup.txt"
        java -jar $MAIN_DIR/runs/quarkus-app/quarkus-run.jar & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/quarkus_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    quarkus_native)
        echo "Testing Quarkus native startup time..."
        echo "Start:" `(date +"%T.%3N")`  >> "log/quarkus_native_startup.txt"
        $MAIN_DIR/runs/quarkusrest_native & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/quarkus_native_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    micronaut)
        echo "Testing Micronaut startup time..."
        echo "Start:" `(date +"%T.%3N")` >> "log/micronaut_startup.txt"
        java -jar $MAIN_DIR/runs/micronautrest.jar & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/micronaut_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    micronaut_native)
        echo "Testing Micronaut native startup time..."
        echo "Start:" `(date +"%T.%3N")` >> "log/micronaut_native_startup.txt"
        $MAIN_DIR/runs/micronautrest_native & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/micronaut_native_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    spring)
        echo "Testing Spring startup time..."
        echo "Start:" `(date +"%T.%3N")` >> "log/spring_startup.txt"
        java -jar $MAIN_DIR/runs/springrest.jar & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/spring_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    spring_jetty)
        echo "Testing Spring_jetty startup time..."
        echo "Start:" `(date +"%T.%3N")` >> "log/spring_jetty_startup.txt"
        java -jar $MAIN_DIR/runs/springrest_jetty.jar & echo $! > $PROCESS_PID_FILE
        ./pinger.sh "log/spring_jetty_startup.txt"
        PID=$(cat $PROCESS_PID_FILE)
        kill $PID
        rm -f $PROCESS_PID_FILE
    ;;
    *)
        echo "Choose among: quarkus, quarkus_native, micronaut, micronaut_native, spring, spring_jetty"
    ;;
esac 