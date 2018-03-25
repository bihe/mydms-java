FROM openjdk:10-jre-slim
LABEL author="henrik@binggl.net"

WORKDIR /opt/mydms

RUN mkdir -p /opt/mydms/uploads && mkdir -p /opt/mydms/store 
COPY ./Api/target/mydms-*.jar /opt/mydms
COPY ./Api/src/main/resources/application.yaml /opt/mydms

EXPOSE 8080
# https://developers.redhat.com/blog/2017/03/14/java-inside-docker/
CMD java $JAVA_OPTIONS -jar /opt/mydms/mydms-1.1.0.jar
