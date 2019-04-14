#!/usr/bin/env bash
docker run -it -d -p 2220:22              --name acme-ci-agent acme/core:1.0
docker run -it -d -p 2221:22 -p 8080:8080 --name acme-ci-server acme/core:1.0
docker run -it -d -p 2222:22 -p 8081:8081 --name acme-repository-server acme/core:1.0
docker run -it -d -p 2223:22 -p 9000:9000 --name acme-sonar-server acme/core:1.0
docker run -it -d -p 2224:22 -p 5432:5432 --name acme-pre-prod-db-server acme/core:1.0
docker run -it -d -p 2225:22 -p 8082:8080 --name acme-pre-prod-app-server acme/core:1.0
