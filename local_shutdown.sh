#!/bin/sh
#We compulsorily need the parameters listener.port , server.port, and device.type 
#Optional parameters are hostname

#Take command line argument -listener_Port
#Do validations, there should be at least 3 arguments




if [ $# -lt 2 ] || [ $# -gt 3 ]
then
       echo "usage: ./$0 <port> <instance_id> <waittime> (Optional)" 
       exit 1
fi

port=$1
instanceId=$2

waittime=5
if [ $# -eq 3 ]
then
       waittime=$3
fi       


logRoot="/var/log/javaapps/vehicleM1M2-dev"
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
	exit 4
fi


echo "Attempting to gracefully shutdown..."
curl -X POST "http://localhost:$port/shutdownContext"
status=$?
echo ""

if [ $status -eq 0 ]
then
	echo "Graceful shutown successfuly initiated"
	echo "waiting $waittime seconds..."
	sleep $waittime
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
			exit 3
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
                exit 3
        fi
fi
	








#Todo add condition for server up - also needs work in java. 
