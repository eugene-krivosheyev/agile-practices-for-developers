# Build and Run
## Install environment
```bash
sudo apt install openjdk-8-jdk-headless
sudo apt install maven
```

## Generate Gradle wrapper (optional)
```
gradle wrapper
```

## Build with Gradle and Run raw distr
```bash
mvn clean verify
cd build/libs
java -jar backend-1.0-SNAPSHOT.jar --spring.profiles.active=test
```

## Build and Run with Gradle
```bash
export SPRING_PROFILES_ACTIVE=test
mvn clean spring-boot:run
```

## Build and Publish to corporate Maven repo
```bash
mvn clean deploy
```

# API Doc
http://localhost:8080/swagger-ui.html

# API Test Use-Cases
file://src/integrationTest/java
