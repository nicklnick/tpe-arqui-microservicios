#!/bin/bash
cd ../../

docker build . -t microservicios/chats-test-unit -f Dockerfile.unit
docker run --rm -it microservicios/chats-test-unit