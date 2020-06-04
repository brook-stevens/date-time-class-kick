# Simple date-time webservice
## Service
This is a simple service that returns the date-time in UTC and to the second for time at /date-time.  The service is a spring boot service written in kotlin and may be run locally with:
* `./gradlew bootRun`
* Then go to:
* `http://localhost:8080/date-time`
* Tests may be run with:
* `./gradlew test`

## Hosting
The service is deployed to a fully terraformed aws fargate cluster with autoscaling groups to control resource usage.  The triggers are cpu based as that is the scalling point of our service.  They are intentionally small containers.
 
## Deployment
Deploying to ECS is done manually through local docker commands.  To build the image:

`docker build --build-arg JAR_FILE=build/libs/time-class-kick-0.0.1-SNAPSHOT.jar -t date-time .`

Then follow instructions in the ECS repo to upload the image to the repository

#Load Testing
Load testing is done using [Gatling](https://gatling.io/) and the scenario can be seen in the: DateTimeWebServiceLoadTestSimulation.scala file
There is a helper script:

`load_test.sh`

That you can use to run the gatling test.  NOTE:  Because my local machine could not generate enough load to get to 1000 rps as part of the environment there is also a c4.large aws linux instance setup:
```bash
sudo amazon-linux-extras enable corretto8
sudo yum -y install java-1.8.0-amazon-corretto
sudo yum -y install java-1.8.0-amazon-corretto-devel
sudo yum install -y git
git clone https://github.com/stevebrook/date-time-class-kick.git

#Setup apache:
sudo yum -y install httpd
sudo service httpd start
sudo ln -s /home/ec2-user/date-time-class-kick/build/gatling-results/ /var/www/html/gatling

#To run the tests
cd date-time-class-kick
./load-test.sh
``` 

This script will print out a url to view the results of the run, [click here for example](http://18.220.118.143/html/gatling/datetimewebserviceloadtestsimulation-20200419185042237/).

The gatling scenario will scale up to 1200 users over 4 minutes.
 
