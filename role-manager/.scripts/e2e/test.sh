#!/bin/bash

docker compose --profile role-manager up --abort-on-container-exit --exit-code-from api-role-manager
