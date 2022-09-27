#!/bin/sh


if [ $# -ne 11 ] && [ $# -ne 12 ]
then
       echo "usage: ./$0 <server_port> <profile> <size Of Log file in MB> <number of rollover files> <logLevel> <instance_id> <log_root> <cloud_config_uri> <cloud_config_username> <cloud_config_password> <jmx_port> <force_kill_timeout> (optional)"
       exit 1
fi



serverPort=$1
instanceId=$6
logRoot=$7
killWaitTime=${12}



currentDir=$(dirname -- $0)

echo $currentDir

echo "Shutting down old process if present..."

sh $currentDir/production_shutdown.sh $serverPort $logRoot $instanceId $killWaitTime
sh $currentDir/production_start.sh $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11}
