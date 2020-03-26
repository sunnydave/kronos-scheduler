# Kronos Scheduler
A quartz based job scheduler API

## Installation

### Database
The project configuration is built for MySql database. You can change it by updating the application.properties file to add drivers and details for your desired database. 
You will need to run the sql scripts under dbscripts folder on the database before running the application. 

### Application
The service requires Java 1.8 and Maven to work. 

## Build

### mvn package
This should create a jar file under the target folder 

### mvn docker:build
In case you want to create a docker image of the service. 
Before you create a docker image, you will need to change the docker.image property in the pom.xml

## API Reference

### /kronos/swagger-ui.html
Once you start the application all API docs should be available at this URL. 