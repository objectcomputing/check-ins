FROM openjdk:14-alpine
WORKDIR /server
COPY /*-all.jar check-ins-server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Dmicronaut.environments=gcp", "-jar", "check-ins-server.jar"]