node{
	//todo - Store port to Device type in map, after all bugs are fixed. 

	def stringParams=['appName','branch','remoteUser','remoteHost','logRoot','profile','logLevel','instanceID','cloudConfigUri','typeOfOperation']
		def intParams=['serverPort','sizeOfLogFiles','numberOfLogFiles']
		//These are parameters whose values are integers, not necessarily which are integer params in jenkins 

		def boolParams=['buildNeeded','copyToServer']

		def paramsMap=[:] 
		def opType=''



		stage ('Parse Params')
		{
			echo this.params.toString()
				echo this.params.getClass().toString()


				//		this.params.each { key,val ->
				//			echo key+" "+val
				//		}

				stringParams.each {key->
					val=this.params.get(key)
						if (val==null)
							error('Got null value for '+key )
								paramsMap.put(key,val.trim())

				}

			intParams.each {key->
				val=this.params.get(key)
					if (val==null)
						error('Got null value for '+key )
					else if (!val.trim().isInteger())
					{
						error('Got non-integer value for '+key + ":"+val.trim() )   
					}
				paramsMap.put(key,val.trim())

			}

			boolParams.each {key->
				val=this.params.get(key)
					if (val==null)
						error('Got null value for '+key )
							paramsMap.put(key,val.toBoolean())
			}


			opType=paramsMap.get('typeOfOperation')

			echo paramsMap.toString()

		}

	stage ('Checkout'){

		if(paramsMap.get("buildNeeded")&& opType!='shutdown')
		{

			checkout([$class: 'GitSCM',branches: [[name: paramsMap.get("branch") ]],userRemoteConfigs: [[credentialsId: 'deployjava', url: 'git@gitlab.com:elogistjavaprojects/eureka-server.git']]])


		}
	}

	stage ('Build'){
		    if(paramsMap.get("buildNeeded") && opType!='shutdown')
                {

		echo "Building consumer..."
			sh "ls -la ${pwd()}"
			sh "ls -la ."
			sh "pwd"

			retVal=sh(returnStatus: true,script:"sh ./build.sh")
			echo "Build returned ${retVal.toString()}"
			if (retVal!=0)
				//		{
				//			error("Build failed with return value ${retVal.toString()}") 
				//		}	
				echo "Finished building...."

		}
	}
	stage('SSH transfer and restart') {

			remoteUser=paramsMap.get('remoteUser')
			remoteHost=paramsMap.get('remoteHost')
			baseDir=paramsMap.get('appName');

			sshLsCommand="ssh -o StrictHostKeyChecking=no   ${remoteUser}@${remoteHost} ls -l ${baseDir}/${paramsMap.get('instanceID')}"

			echo sshLsCommand

			mkdirCommand="ssh -o StrictHostKeyChecking=no   ${remoteUser}@${remoteHost} mkdir -p ${baseDir}/${paramsMap.get('instanceID')}"
			echo mkdirCommand



			rsyncCommand="rsync -e \'ssh -o StrictHostKeyChecking=no\' --recursive --times --compress --delete --progress build/libs/ ${remoteUser}@${remoteHost}:${baseDir}/${paramsMap.get('instanceID')}"
			echo rsyncCommand

			if(opType=='restart')
                        {
				sshRunCommand="ssh -o StrictHostKeyChecking=no   ${remoteUser}@${remoteHost} sh ${baseDir}/${paramsMap.get('instanceID')}/production_restart.sh ${paramsMap.get('serverPort')} ${paramsMap.get('profile')} ${paramsMap.get('sizeOfLogFiles')} ${paramsMap.get('numberOfLogFiles')} ${paramsMap.get('logLevel')} ${paramsMap.get('instanceID')} ${paramsMap.get('logRoot')} ${paramsMap.get('cloudConfigUri')} " 
			}
			else if(opType=='start')
                        {
                                sshRunCommand="ssh -o StrictHostKeyChecking=no   ${remoteUser}@${remoteHost} sh ${baseDir}/${paramsMap.get('instanceID')}/production_start.sh ${paramsMap.get('serverPort')} ${paramsMap.get('profile')} ${paramsMap.get('sizeOfLogFiles')} ${paramsMap.get('numberOfLogFiles')} ${paramsMap.get('logLevel')} ${paramsMap.get('instanceID')} ${paramsMap.get('logRoot')} ${paramsMap.get('cloudConfigUri')} "
                        }
			 else if(opType=='shutdown')
                        {
                                sshRunCommand="ssh -o StrictHostKeyChecking=no   ${remoteUser}@${remoteHost} sh ${baseDir}/${paramsMap.get('instanceID')}/production_shutdown.sh ${logRoot} ${paramsMap.get('instanceID')}"
                        }
                        else if (optype!='none')
			{
                                error ('invalid typeOfOperation!')
                        }

			echo sshRunCommand


			//todo move the mkdir and ls command to outside the try block. 
				sshagent(credentials : ['deployjava']) {

					//todo add checks for return values here
					//make directory if it doesnt exist
					if(opType!='shutdown' && paramsMap.get('copyToServer'))
                                        {

						sh mkdirCommand
						//This is a test command
						sh sshLsCommand
						//This is the actual copy command
						sh rsyncCommand
					}
						
					if (opType!='none')
					{
						sh sshRunCommand
					}

				}

		}
	     
}

