#!/bin/bash
cd ../../

docker build . -t microservicios/api-gateway-test-unit -f Dockerfile.unit
docker run --rm microservicios/api-gateway-test-unit
