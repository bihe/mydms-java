#!/bin/sh
docker pull openjdk:9-slim
docker build -t mydms .
