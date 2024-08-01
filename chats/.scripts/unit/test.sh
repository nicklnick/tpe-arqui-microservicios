#!/bin/bash
cd ../../

sudo docker build . -t microservicios/chats-test-integration -f Dockerfile.integration
sudo docker run --rm -it microservicios/chats-test-integration
