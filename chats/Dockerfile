# Use the official .NET SDK image to build the app
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /app

# Copy the project file and restore dependencies
COPY src/ChatsApi/*.csproj ./src/Api/
RUN dotnet restore src/Api/ChatsApi.csproj

# Copy the entire project and build the app
COPY src/ChatsApi/ ./src/Api/
WORKDIR /app/src/Api
RUN dotnet publish -c Release -o out

# Use the official ASP.NET Core runtime image
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app
COPY --from=build /app/src/Api/out ./

# Expose the port the app runs on
EXPOSE 8080


# Set the entry point to run the app
ENTRYPOINT ["dotnet", "ChatsApi.dll"]
