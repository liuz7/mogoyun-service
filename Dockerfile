FROM openjdk:8u171-jre-alpine
ENV PORT 4567
EXPOSE 4567
COPY build/libs/*.jar /opt/
WORKDIR /opt
CMD ["/bin/sh", "-c", "find -type f -name '*.jar' | xargs java -jar -Dserver.port=${PORT}"]
