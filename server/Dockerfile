FROM openjdk:14-alpine
COPY /build/libs/*-all.jar check-ins-server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Dmicronaut.environments=gcp","-noverify ${JAVA_OPTS}", "-jar", "check-ins-server.jar"]