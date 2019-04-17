# Build and Run
## Install environment
```bash
sudo apt install openjdk-8-jdk-headless
sudo apt install maven
sudo apt install gradle
```

## Generate Gradle wrapper (optional)
```
gradle wrapper
```

## Build with Gradle and Run raw distr
```bash
./gradlew clean build
cd build/libs
java -jar backend-1.0-SNAPSHOT.jar --spring.profiles.active=test
```

## Build and Run with Gradle
```bash
export SPRING_PROFILES_ACTIVE=test
./gradlew clean bootRun
```

## Build and Publish to corporate Maven repo
```bash
./gradlew clean build publish
```
Then access [full distr at Nexus repo](https://[repository-server:8081]/service/rest/repository/browse/maven-snapshots/com/acme/backend/) with account admin
You need latest build - archive from
```bash
/1.0-DATE-BUILD/backend-1.0-DATE-BUILD.zip
                                       ===
```
Then run as usual raw distr.

# API Doc
http://localhost:8080/swagger-ui.html

# API Test Use-Cases
file://src/integrationTest/java
