#!/bin/bash

cd ../../

dotnet test --filter TestProject1.ChatServiceUnitTests
if [ $? -eq 0 ]; then
  dotnet test --filter TestProject1.ChatsControllerTests
else
  exit 1
fi
