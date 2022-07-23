FROM openjdk:17.04
COPY ./out/artifacts/GodBot_jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "GodBot.jar"]
