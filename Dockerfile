FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./build/libs/permit-server-0.0.1-SNAPSHOT.jar /app/APP.jar
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar",  "APP.jar"]
