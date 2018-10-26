FROM openjdk:8u171-jre-alpine
ARG target_file
ENV PORT 4567
EXPOSE 4567
COPY ${target_file} /opt/
WORKDIR /opt
CMD ["/bin/sh", "-c", "find -type f -name '*.jar' | xargs java -jar -Dserver.port=${PORT}"]
