#!/bin/sh


if [ $# -lt 3 ] || [ $# -gt 4   ]
then
	echo "usage: ./$0 <port_number> <log_root> <instance_id> <kill_wait> (optional)"
       	exit 1
fi

echo "Trying graceful shutdown..."

port=$1
logRoot=$2
instanceId=$3
if [ $# -eq 4 ]
then
	killTimeout=$4
else
	killTimeout=10
fi



logging_dir=$logRoot/$instanceId
pid=`cat $logging_dir/pid.txt`

echo "Process pid: $pid"

if ! [ "$pid" -eq "$pid" ] 2>/dev/null
then
	echo "Could not find pid"
	exit 2
fi



if !  ps -p $pid > /dev/null
then
        echo "Process not running or already shut down. Exiting."
        exit 3
fi

echo "Attempting to gracefully shutdown..."
curl -X POST "http://localhost:$port/shutdownContext"
status=$?
echo ""

if [ $status -eq 0 ]
then
        echo "Graceful shutown successfuly initiated"
        echo "waiting $killTimeout seconds..."
        sleep $killTimeout
        nc -z localhost $port
        portOpen=$?
        if  [ $portOpen -ne 0 ]
        then
                echo "Verified that port closed, but will still check if pid is running"
        else
                echo "Port is still open! Checking if pid is running..."
        fi
        if  ps -p $pid > /dev/null
        then
                echo "Process is still running, going to kill it with -9"
                kill -9 $pid
                if [ $? -eq 0 ]
                then
                        echo "Killed process $pid successfully"
                else
                        echo "Could not kill process $pid"
                        exit 4
                fi
        else
                echo "Process $pid not running, successfully stopped by graceful shutdown"
        fi
        mv $logging_dir/pid.txt $logging_dir/pid.txt.bak
else
        echo "Graceful shutdown command failed, falling back to kill -9"
        kill -9 $pid
        if [ $? -eq 0 ]
        then
                echo "Killed process $pid successfully"
        else
                echo "Could not kill process $pid"
                exit 5
        fi
fi

exit 0
