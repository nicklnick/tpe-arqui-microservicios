#!/bin/bash
cd ../../

docker build . -t microservicios/message-history-test-unit -f Dockerfile.unit
docker run --rm microservicios/message-history-test-unit