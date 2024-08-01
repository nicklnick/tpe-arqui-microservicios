#!/bin/bash
cd ../../

sudo docker build . -t microservicios/chats-test-unit -f Dockerfile.unit
sudo docker run --rm -it microservicios/chats-test-unit