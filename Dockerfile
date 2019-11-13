FROM openjdk:8-jre-slim

EXPOSE 1099
EXPOSE 1100

ENV FOO=localhost

RUN mkdir /app

COPY build/libs/*.jar /app/distributedhello.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom", "-Djava.util.logging.SimpleFormatter.format=%1$tc %2$s %4$s: %5$s%6$s%n", "-jar", "/app/distributedhello.jar"]