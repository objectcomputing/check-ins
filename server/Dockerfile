FROM adoptopenjdk/openjdk11:latest
COPY build/libs/*.jar check-ins-server.jar
EXPOSE 8080
CMD java  -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar check-ins-server.jar