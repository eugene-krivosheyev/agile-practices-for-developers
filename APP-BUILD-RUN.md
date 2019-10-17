# Build and Run
## Install environment
```bash
sudo apt install openjdk-8-jdk-headless
sudo apt install maven
```

## Build with Maven and Run raw distr
```bash
mvn clean verify
java -jar target/dbo-1.0-SNAPSHOT.jar --spring.profiles.active=it
```

# API Doc
http://localhost:8080/dbo/swagger-ui.html

# Build Docker container for Application
```bash
docker build -t acme/dbo:1.0-SNAPHOT-it .
```

# Run Docker container for Application
```bash
docker run -it -d -p 8080:8080 --name dbo acme/dbo:1.0-SNAPHOT-it
docker attach dbo
docker exec -it dbo /bin/sh
docker rm dbo -f
```
