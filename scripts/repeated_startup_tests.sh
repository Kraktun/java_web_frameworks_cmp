#!/bin/bash

APP=$1
REPETITIONS=$2

# note: don't source sdkman or it restores the default jdk
JAVA_VER=$(java -version 2>&1)
echo "Java version:"
echo $JAVA_VER

for (( i = 1; i <= $REPETITIONS; i++ ))
do
    echo "##############################"
    echo -e "\tRepetition: $i"
    echo "##############################"
    ./startup_test.sh $APP
    sleep 5 # to cool down things
done

