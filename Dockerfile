
FROM azul/zulu-openjdk:11
EXPOSE 9191

COPY build/libs/app.jar app.jar
CMD ["java", "-jar", "app.jar"]
