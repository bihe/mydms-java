## fronted build-phase
FROM node:10-alpine AS FRONTEND-BUILD
WORKDIR /frontend-build
COPY ./UI .
RUN npm install -g @angular/cli@latest && npm install && npm run build -- --prod --base-href /ui/

## backend build-phase
FROM maven:3-jdk-11-slim AS BACKEND-BUILD
ARG buildtime_variable_build=2.0.0.0
ARG buildtime_variable_timestamp=YYYYMMDD
ENV BUILD_TIMESTAMP=${buildtime_variable_timestamp}
ENV BUILD_BUILDNUMBER=${buildtime_variable_build}
WORKDIR /backend-build
COPY ./Api .
RUN sed -i "s/@timestamp@/$BUILD_TIMESTAMP/" ./src/main/resources/version.properties && sed -i "s/@buildNumber@/$BUILD_BUILDNUMBER/" ./src/main/resources/version.properties
COPY --from=FRONTEND-BUILD /frontend-build/dist  ./src/main/resources/static/ui
RUN set MYDMS_TMP_PATH=./target && mvn clean package

FROM openjdk:11-jre-slim
LABEL author="henrik@binggl.net"
WORKDIR /opt/mydms
RUN mkdir -p /opt/mydms/uploads && mkdir -p /opt/mydms/store 
COPY --from=BACKEND-BUILD /backend-build/target/mydms-*.jar /opt/mydms
COPY ./Api/src/main/resources/application.yaml /opt/mydms
RUN ls -la /opt/mydms
EXPOSE 8080
CMD java $JAVA_OPTIONS -jar /opt/mydms/mydms-1.2.0.jar
