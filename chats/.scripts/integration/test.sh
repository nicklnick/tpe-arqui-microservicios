#!/bin/bash
cd ../../

docker build . -t microservicios/chats-test-integration -f Dockerfile.integration
docker run --rm microservicios/chats-test-integration