#!/usr/bin/env bash
docker run -it -d -p 2220:22 -p 8081:8081 -p 8080:8080 -p 9000:9000 -p 5432:5432 --name acme-ci-server acme/core:1.0
docker run -it -d -p 2221:22 -p 8082:8081 -p 5433:5432							--name acme-test-server acme/core:1.0
