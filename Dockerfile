FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./build/libs/permit-server-0.0.1-SNAPSHOT.jar /app/APP.jar
#docker run할때 각 환경에 맞게 변경
ENV SPRING_PROFILES_ACTIVE=dev
CMD ["sh", "-c", "java -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar APP.jar"]
