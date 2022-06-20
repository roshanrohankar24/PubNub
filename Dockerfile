FROM openjdk:8
EXPOSE 9001
ADD target/pubnub-0.0.1-SNAPSHOT.jar pubnub-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/pubnub-0.0.1-SNAPSHOT.jar"]