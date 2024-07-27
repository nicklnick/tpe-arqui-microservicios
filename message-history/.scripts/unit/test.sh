#!/bin/bash

cd ../../

dotnet test --filter Tests.MessageHistoryServiceTests
if [ $? -eq 0 ]; then
  dotnet test --filter Tests.MessageHistoryControllerTests
else
  exit 1
fi
