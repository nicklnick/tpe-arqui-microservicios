#!/bin/bash

echo "$CI_JOB_TOKEN" | docker login -u $CI_REGISTRY_USER --password-stdin $CI_REGISTRY

docker run $ROLE_MANAGER_IMAGE_NAME test