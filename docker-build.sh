#!/bin/sh
docker pull openjdk:8-jdk-alpine
docker build -t mydms .
