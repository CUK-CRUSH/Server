FROM openjdk:17

COPY ./build/libs/myList-0.0.1-SNAPSHOT.jar /myList-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/myList-0.0.1-SNAPSHOT.jar"]
