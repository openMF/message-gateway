FROM openjdk:8
EXPOSE 9191

COPY build/libs/*.jar .
CMD java -jar *.jar
