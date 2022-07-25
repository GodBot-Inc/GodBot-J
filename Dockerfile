FROM openjdk:17
COPY ./out/artifacts/GodBot_jar/GodBot.jar /tmp
COPY .env /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "GodBot.jar"]
