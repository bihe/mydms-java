#!/bin/sh

docker pull mariadb:10.2

docker run --restart unless-stopped \
-p 127.0.0.1:3306:3306 \
--user 112 \
--name application-db \
-v /var/lib/mysql:/var/lib/mysql \
-d mariadb:10.2

