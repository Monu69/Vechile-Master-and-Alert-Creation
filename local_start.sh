#!/bin/sh
#We compulsorily need the parameters in server.port, and profile,loglevel
#Optional parameters are hostname

if [ $# -ne 3 ]
then
       echo "usage: ./$0 <server_port> <profile>  <logLevel>"
       exit 1
fi


server_port=$1
profile=$2
logLevel=$3

sh ./gradlew bootJar





# Logging_dir is for localhost. 

instanceId=1
sizeOfLogFile="1024 MB"
rollOverFiles=1000
logging_dir=/var/log/javaapps/vehicleM1M2-dev/$instanceId

mkdir -p $logging_dir
. /etc/profile

infoLogFileName="vehicleM1M2-dev-all.log"
warnLogFileName="VehicleM1M2-dev-warn.log"


nohup java -Dspring.cloud.config.uri=http://dev1.xswift.biz:8085 -Dspring.cloud.config.username=root -Dspring.cloud.config.password=s3cr3t  -Dspring.profiles.active=$profile  -Dlog.level=$logLevel -Dmax.file.size="$sizeOfLogFile" -Dlog.info.file.name="$logging_dir/$infoLogFileName" -Dlog.warn.file.name="$logging_dir/$warnLogFileName" -Dlog.info.file.pattern="$logging_dir/%d{yyyy-MM}/vehicleM1M2-dev-all-%d{yyyy-MM-dd}-%03i.log.gz" -Dlog.warn.file.pattern="$logging_dir/%d{yyyy-MM}/vehicleM1M2-dev-warn-%d{yyyy-MM-dd}-%03i.log.gz" -Drollover.files=$rollOverFiles -Dserver.port=$server_port  -Xmx1024m -jar ./build/libs/vehicle_master_and_alert_creation-0.0.1-SNAPSHOT.jar  > $logging_dir/out.txt 2>$logging_dir/err.txt  &
echo !!
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
exit 0
#Todo add condition for server up - also needs work in java. 
