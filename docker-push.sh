#!/bin/bash

docker tag mydms $DOCKER_ID_USER/mydms
docker push $DOCKER_ID_USER/mydms