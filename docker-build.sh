#!/bin/sh

docker pull openjdk:9-jdk-slim
docker build -t mydms .
