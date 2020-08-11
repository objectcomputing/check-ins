FROM openjdk:14-alpine
COPY /build/libs/*-all.jar check-ins-server.jar
EXPOSE 80 8080
# CMD ["java", "-Dcom.sun.management.jmxremote", "-Dmicronaut.environments=gcp", "-jar", "check-ins-server.jar"]
# don't start the jar