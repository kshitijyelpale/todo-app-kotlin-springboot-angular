FROM openjdk:21-jdk
EXPOSE 9090
ARG JAR_FILE=build/libs/todo-app-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} todo-app-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/todo-app-0.0.1-SNAPSHOT.jar"]
