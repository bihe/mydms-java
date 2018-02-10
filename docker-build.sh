#!/bin/sh

docker pull anapsix/alpine-java:8
docker build -t mydms .
