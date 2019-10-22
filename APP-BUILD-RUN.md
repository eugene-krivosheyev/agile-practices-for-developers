# Build and Run
## Build with Maven and Run raw release locally
```bash
mvn clean verify -Dlogback.configurationFile=logback.xml
java -Dderby.stream.error.file=log/derby.log -jar target/dbo-1.0-SNAPSHOT.jar --spring.profiles.active=qa
```

# API Doc
http://localhost:8080/dbo/swagger-ui.html

## Deploy artifact to Artifact Server
```bash
mvn deploy -s maven-settings.xml
```

# Build and run Docker container for Application
```bash
docker build -t acme/dbo:1.0-SNAPHOT-it .

docker run -it -d -p 8080:8080 --name dbo acme/dbo:1.0-SNAPHOT-it
docker attach dbo
docker exec -it dbo /bin/sh

docker rm dbo -f
```

# Run release remotely
```bash
cd /dbo
java -jar dbo-1.0-SNAPSHOT.jar
```