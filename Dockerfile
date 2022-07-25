FROM openjdk:17
COPY GodBot.jar /tmp
COPY .env /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "GodBot.jar"]
