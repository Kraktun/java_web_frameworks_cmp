#!/bin/bash
APP_PID=$1
FILE_OUT=$2

rm -f ${FILE_OUT}_cpu.txt

while true; do 
    top -b -n 1 -d 0.02 -p $APP_PID | tail -1 | awk '{print $9}' >> ${FILE_OUT}_cpu.txt
    sleep 0.2
done
