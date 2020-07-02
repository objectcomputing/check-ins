FROM openjdk:14-alpine
COPY build/libs/*.jar check-ins-server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "check-ins-server.jar"]