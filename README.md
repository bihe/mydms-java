# mydms-java

[![Build Status](https://travis-ci.org/bihe/mydms-java.png)](https://travis-ci.org/bihe/mydms-java)

Simple application to upload, store, search documents and meta-data.

## Structure

The basic structure of 'mydms' is a REST backend by [spring-boot](https://projects.spring.io/spring-boot/) using [kotlin](https://kotlinlang.org/), meta-data is kept in [mariadb](https://mariadb.org/), documents stored in [S3](https://aws.amazon.com/s3/) and the frontend provided via [angular](https://angular.io/). 

## Technology

* REST backend: spring-boot (2.0.0.RELEASE), kotlin (1.2.30)
* frontend angular (5.2.0)
* mariadb: 10.x

## Build
The REST Api and the UI can be built separately. 

### UI
`npm run build -- --prod --bh /ui/`

### Api
`./mvnw package`
  
### All
For convenience a top-level build-script is available: `./build.sh`

A Dockerfile is present to deploy the application as a container image (https://hub.docker.com/r/bihe/mydms/).

## Why

I needed something to keep track of my scanned invoices. Being a software nerd, I created a solution for this purpose. The added benefit for me is, that I have a technology playground to try out new things. 

There are different versions/iterations available.

* [mydms-node](https://github.com/bihe/myDMS-node) - very early adventures in node.js
* [mydms-java (dropwizard)](https://github.com/bihe/mydms-java/tree/dropwizard) - use dropwizard as the REST backend and documents were stored in Google Drive

