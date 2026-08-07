FROM eclipse-temurin:21-jre
# 9191 = Spring Boot / actuator, 5009 = Camel REST (undertow).
# callbackconfig.port (5000) is outbound only, so it is not exposed.
EXPOSE 9191 5009

COPY build/libs/app.jar app.jar
CMD ["java", "-jar", "app.jar"]
