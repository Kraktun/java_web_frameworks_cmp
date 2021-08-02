#!/bin/bash
APP_PID=$1
FILE_OUT=$2

rm -f ${FILE_OUT}_rss.txt

while true; do 
    ps -o rss -p $APP_PID | tail -1 >> ${FILE_OUT}_rss.txt
    #sleep 0.15 # time top takes to compute its value, to align the two outputs # removed to do it on the plot
    sleep 0.2
done
