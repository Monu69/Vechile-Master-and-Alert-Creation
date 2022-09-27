#!/bin/sh
#We compulsorily need the parameters listener.port , server.port, and device.type 
#Optional parameters are hostname

#Take command line argument -listener_Port
#Do validations, there should be at least 3 arguments

if [ $# -ne 11 ]
then
       echo "usage: ./$0 <server_port> <profile> <size Of Log file in MB> <number of rollover files> <logLevel> <instance_id> <log_root> <cloud_config_uri> <cloud_config_user_name> <cloud_config_password> <jmx_port>"
       exit 1
fi



server_port=$1
profile=$2
sizeOfLogFile="$3 MB"
rollOverFiles=$4
logLevel=$5
instanceID=$6
logging_dir=$7/$instanceID
cloudConfigUri=$8
cloudConfigUserName=$9
cloudConfigPassword=${10}
jmxPort=${11}

currentDir=$(dirname -- $0)
echo $currentDir


# Logging_dir is for localhost. 
mkdir -p $logging_dir
. /etc/profile


infoLogFileName="vehicleM1M2-dev-all.log"
warnLogFileName="vehicleM1M2-dev-warn.log"


nohup java  -Dspring.cloud.config.uri=$cloudConfigUri -Dspring.cloud.config.username=cloudConfigUserName -Dspring.cloud.config.password=cloudConfigPassword -Dspring.profiles.active=$profile  -Dmax.file.size="$sizeOfLogFile" -Dlog.info.file.name="$logging_dir/$infoLogFileName" -Dlog.warn.file.name="$logging_dir/$warnLogFileName" -Dlog.info.file.pattern="$logging_dir/%d{yyyy-MM}/vehicleM1M2-dev-all-%d{yyyy-MM-dd}-%03i.log.gz" -Dlog.warn.file.pattern="$logging_dir/%d{yyyy-MM}/vehicleM1M2-dev-warn-%d{yyyy-MM-dd}-%03i.log.gz" -Drollover.files=$rollOverFiles -Dlog.level=$logLevel -Dserver.port=$server_port -Xmx4048m  -Dcom.sun.management.jmxremote.host=127.0.0.1 -Dcom.sun.management.jmxremote.port=$jmxPort -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false  -Djava.rmi.server.hostname=127.0.0.1   -jar $currentDir/vehicle_master_and_alert_creation-0.0.1-SNAPSHOT.jar  > $logging_dir/out.txt 2>$logging_dir/err.txt  &
echo $! > $logging_dir/pid.txt

if [ ! -f $logging_dir/$infoLogFileName ]
	then 
        echo "Waiting for file to get created"
        sleep 5
else
	echo "Log file present, displaying"
fi
sleep 5
timeout 30 tail -f $logging_dir/$infoLogFileName

#Todo add condition for server up - also needs work in java.
return 0
