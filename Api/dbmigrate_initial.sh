#!/bin/sh
java -jar target/mydms-1.0-SNAPSHOT.jar db drop-all --confirm-delete-everything  mydms.yml
java -jar target/mydms-1.0-SNAPSHOT.jar db migrate mydms.yml
