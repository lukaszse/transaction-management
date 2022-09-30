FROM gradle:7.5.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-alpine
EXPOSE 443
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
ENTRYPOINT ["java", "-jar" ,"/app/spring-boot-application.jar"]

#FROM openjdk:17-jdk-alpine
#RUN addgroup -S carrental && adduser -S carrental -G carrental
#WORKDIR /simple-bills/
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#EXPOSE 443
#ENTRYPOINT ["java","-jar","app.jar"]