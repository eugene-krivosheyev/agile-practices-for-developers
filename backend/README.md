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
http://localhost:8080/swagger-ui.html

# CI
- [Jenkins](http://184.72.144.118:8080/jenkins/)
- [Sonar](http://184.72.144.118:9000)
