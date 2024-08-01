#!/bin/bash
cd ../../

docker build . -t microservicios/chats-test-unit -f Dockerfile.unit
docker run --rm microservicios/chats-test-unit
