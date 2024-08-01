#!/bin/bash
cd ../../

docker build . -t microservicios/message-history-test-integration -f Dockerfile.integration
docker run --rm microservicios/message-history-test-integration