FROM openjdk:9-jdk-slim
LABEL author="henrik@binggl.net"
WORKDIR /opt/mydms
COPY ./Api/target/mydms-1.0.0-SNAPSHOT.jar /opt/mydms
COPY ./Api/src/main/resources/application.yaml /opt/mydms

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/mydms/mydms-1.0.0-SNAPSHOT.jar"]

