FROM adoptopenjdk/openjdk8:latest
COPY build/libs/*.jar google-drive-upload.jar
EXPOSE 8080
CMD java  -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar google-drive-upload.jar