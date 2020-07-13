folder('Groovy') {
    displayName('Groovy')
    description('Folder created by groovy for deployment of project')
}

folder('Groovy/Task-6') {
	displayName('Task-6')
    description('Folder containing all Task-4 jobs')
	
	job("Groovy/Task-6/job1")
	{
		description ("job 1")
		
		scm {
				github ('NikhilSuryawanshi/Devops-Task-4-groovy','master')
			}
		configure { it / 'triggers' / 'com.cloudbees.jenkins.GitHubPushTrigger' / 'spec' }
		steps{
		shell('sudo cp -rvf * /root/devops-task-6/')
			}
	}
	
	job("Groovy/Task-6/job2")

	{

		description ("job 2")

		steps{

				shell('''
					if sudo ls /root/devops-task-6/web-pages/ | grep .php
					then
					 if sudo kubectl get pvc | grep pvc
					 then
						echo "mydeploy pvc already created"
					 else
						sudo kubectl delete deploy mydeploy
						sudo kubectl create -f /root/devops-task-6/kube-file/mydeploy-pvc.yaml 
					 fi
					 if sudo kubectl get deploy | grep mydeploy
					 then
					   echo "mydeploy running perfectly"
					 else
					   sudo kubectl create -f /root/devops-task-6/kube-file/mydeploy.yaml 
					 fi
					else
					 echo "no webpage found"
					fi
					sleep 60
					podpath=$(sudo kubectl get -o jsonpath="{.spec.ports[0].nodePort}" services mydeploy)
					podname=$(sudo kubectl get pod -l app=mydeploy -o jsonpath="{.items[0].metadata.name}" )
					sudo kubectl cp /root/devops-task-6/web-pages/index.php  $podname:/var/www/html/
					echo "goto http://192.168.99.101:$podpath"
				''')
		
			}
			 triggers {

				upstream('job1', 'SUCCESS')

			}
	}
	job("Groovy/Task-6/job3")
		{
			description ("job 3")
			steps{
			shell('''
				status=$(curl -o /dev/null -sw "%{http_code}" http://192.168.99.101:32000)
				if [[$status == 200 ]]
				then
				exit 1
				else
				echo "Working great"
				fi 
				''')
				}
				
			publishers {
			   mailer('nikhilsurya29@gmail.com', true, true)
		   }
			triggers {
				   upstream('job2', 'SUCCESS')
				}
		}


		}
