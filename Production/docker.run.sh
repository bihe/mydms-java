docker network create -d bridge app-network


#!/bin/bash
source ./.environment

docker pull bihe/mydms

docker run --restart unless-stopped -d -p 127.0.0.1:8080:8080 --user 999 \
-m 350m \
--name mydms-app \
--network=app-network \
-e JAVA_OPTIONS='-Xms128m -Xmx350m -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap' \
-v /var/www/docker/mydms/uploads:/opt/mydms/uploads \
--mount source=mydms-store,target=/opt/mydms/store \
--mount source=mydms-logs,target=/opt/mydms/logs \
-e MYDMS_BASE_URL \
-e MYDMS_JWT_TOKEN_SECRET \
-e MYDMS_DB_USER \
-e MYDMS_DB_PASS \
-e MYDMS_DB_HOST \
-e MYDMS_DB_PORT \
-e MYDMS_DB_NAME \
-e MYDMS_STORE_PATH \
-e MYDMS_TMP_PATH \
-e MYDMS_AWS_ACCESS_KEY \
-e MYDMS_AWS_SECRET_KEY \
-e MYDMS_AWS_BUCKET_NAME \
-e MYDMS_DETAILED_ERRORS \
-e SPRING_PROFILES_ACTIVE \
bihe/mydms
