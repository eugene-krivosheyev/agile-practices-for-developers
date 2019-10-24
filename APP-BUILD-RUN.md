# Build with Maven and Run raw release locally
```bash
./mvnw verify -T 1C -DdependencyLocationsEnabled=false -Dlogback.configurationFile=logback.xml -Djava.awt.headless=true
java -Dderby.stream.error.file=log/derby.log -jar target/dbo-1.0-SNAPSHOT.jar --spring.profiles.active=qa
```
- http://localhost:8080/dbo/swagger-ui.html

# Deploy artifact to Artifact Server
```bash
./mvnw deploy -T 1C -s Iaac/ansible/files/maven-settings.xml -DskipTests -DdependencyLocationsEnabled=false -Dlogback.configurationFile=logback.xml -Djava.awt.headless=true
```

# Build and run Docker container for Application
```bash
docker build -t acme/dbo:1.0-SNAPHOT-it .

docker run -it -d -p 8080:8080 --name dbo acme/dbo:1.0-SNAPHOT-it
docker attach dbo
docker exec -it dbo /bin/sh

docker rm dbo -f
```

# Graceful shutdown
```
curl --request POST http://localhost:8080/dbo/actuator/shutdown
```
