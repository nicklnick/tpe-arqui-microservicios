#!/bin/bash
cd ../../

docker build . -t microservicios/chat-session-test-unit -f Dockerfile.unit
docker run --rm microservicios/chat-session-test-unit
