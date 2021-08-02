#!/bin/bash

SERVICE_NAME=$1
APP_PATH=$2
YOUR_COMMAND=$3

source set_main_dir.sh

PATH_TO_LIBPROC=$MAIN_DIR/scripts/utils/procname/libprocname.so
PROCESS_NAME='stage_test'
PROCESS_PID_FILE='stage_test_pid.pid'

RSS_PID='rss.pid'
CPU_PID='cpu.pid'

FILE_OUT="log/${SERVICE_NAME}"
mkdir -p log

# remove pid file if it exists
rm -f $PROCESS_PID_FILE

LD_PRELOAD=$PATH_TO_LIBPROC PROCNAME=$PROCESS_NAME $YOUR_COMMAND > /dev/null & echo $! > $PROCESS_PID_FILE

# get pid
PID_T=$(cat $PROCESS_PID_FILE)

# start rss log
chmod u+x get_mem.sh
./get_mem.sh $PID_T $FILE_OUT & echo $! > $RSS_PID

# start cpu log
chmod u+x get_cpu.sh
./get_cpu.sh $PID_T $FILE_OUT & echo $! > $CPU_PID



# wait for input to kill processes
while true; do
    echo -en "Press q to exit \t\t: "
    read input
    if [[ $input = "q" ]] ;then 
        # first kill rss and cpu
        PID=$(cat $RSS_PID);
        if ps -p $PID > /dev/null
		then
			kill $PID
		fi
        rm -f $RSS_PID

        PID=$(cat $CPU_PID);
        if ps -p $PID > /dev/null
		then
			kill $PID
		fi
        rm -f $CPU_PID
        sleep 1

        # then kill process
        if pgrep -x $PROCESS_NAME > /dev/null
		then
            echo "$SERVICE_NAME stopping ..."
            pkill $PROCESS_NAME;
			sleep 2
        else
            echo "$SERVICE_NAME not found, trying pid..."
        fi
        PID=$(cat $PROCESS_PID_FILE);
        if ps -p $PID > /dev/null
		then
			kill $PID
			sleep 1
		fi
		if pgrep -x $PROCESS_NAME > /dev/null
		then
			echo "$SERVICE_NAME stopped ..."
		fi
        rm -f $PROCESS_PID_FILE
        break 
    else 
        echo "Invalid Input."
    fi
done
