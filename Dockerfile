FROM anapsix/alpine-java:8
LABEL author="henrik@binggl.net"
WORKDIR /opt/mydms

RUN mkdir -p /opt/mydms/uploads && mkdir -p /opt/mydms/store

COPY ./Api/target/mydms-1.0.0-SNAPSHOT.jar /opt/mydms
COPY ./Api/src/main/resources/application.yaml /opt/mydms

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "/opt/mydms/mydms-1.0.0-SNAPSHOT.jar"]

