FROM adoptopenjdk/openjdk11:latest
COPY build/libs/*.jar check-ins-server.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "check-ins-server.jar"]