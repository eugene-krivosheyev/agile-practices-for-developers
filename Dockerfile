FROM fabric8/java-alpine-openjdk8-jre:1.6.3
EXPOSE 8080
RUN mkdir -p /dbo
WORKDIR /dbo
ENTRYPOINT ["java", "-jar", "dbo-1.0-SNAPSHOT.jar", "--spring.profiles.active=it"]
COPY target/dbo-1.0-SNAPSHOT.jar /dbo
