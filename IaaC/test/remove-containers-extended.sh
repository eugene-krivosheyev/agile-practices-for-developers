#!/usr/bin/env bash
docker rm acme-pre-prod-app-server -f
docker rm acme-pre-prod-db-server -f
docker rm acme-sonar-server -f
docker rm acme-repository-server -f
docker rm acme-ci-server -f
docker rm acme-ci-agent -f
