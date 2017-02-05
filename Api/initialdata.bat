@echo off
set MYDMS_CREATE_INITIAL_DATA=true && java -jar target/mydms-1.0-SNAPSHOT.jar initialData mydms.yml
