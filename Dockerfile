FROM openjdk:8-jdk-alpine
LABEL author="henrik@binggl.net"

#RUN /usr/sbin/groupadd -g 999 mydms && \
#    /usr/sbin/useradd -r -u 999 -g mydms mydms

WORKDIR /opt/mydms

RUN mkdir -p /opt/mydms/uploads && mkdir -p /opt/mydms/store 
COPY ./Api/target/mydms-1.0.0-SNAPSHOT.jar /opt/mydms
COPY ./Api/src/main/resources/application.yaml /opt/mydms

#RUN chown -R mydms:mydms /opt/mydms

EXPOSE 8080
#USER mydms:mydms
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/mydms/mydms-1.0.0-SNAPSHOT.jar"]
