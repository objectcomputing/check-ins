FROM openjdk:14-alpine
COPY /build/libs/*-all.jar onboard-server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Dmicronaut.environments=gcp", "-jar", "onboard-server.jar"]