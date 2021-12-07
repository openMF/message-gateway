FROM openjdk:11
EXPOSE 9191

COPY build/libs/*.jar .
CMD java -jar *.jar
