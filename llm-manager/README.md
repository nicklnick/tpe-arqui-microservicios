## Build
Inside the microservice root directory, run ``docker buildx build -t llm-manager:latest .`` to build the image.

## Environment
Create a `.env` file inside the microservice root directory with the following variables:
### RabbitMQ
* `RABBITMQ_HOST`: Should be the rabbitmq container name.
* `RABBITMQ_USERNAME`: The defined username.
* `RABBITMQ_PASSWORD`: The defined password.
* `RABBITMQ_PORT`: The port rabbitmq container is running.
* `RABBITMQ_INPUT_QUEUE`: The input queue that will be declared for inputs consuming.
* `RABBITMQ_OUTPUT_QUEUE`: An already existent queue to put the responses.
### GeminiAI
* `GEMINI_GOOGLE_API_KEY`: The Gemini model api key.

## Run
### Prerequisites
* An internal `network` must be created.

* A `rabbitmq` container with a defined name and connected to the `network` must exist.
* The `rabbitmq` container mut have an existent queue for responses purpose.
* A `.env` file following the instructions mentioned above must exist.

### Running application
Running the following command will create a `Faststream` rabbitmq application consuming from the `RABBITMQ_INPUT_QUEUE` and putting responses into `RABBITMQ_OUTPUT_QUEUE`.
```
docker run --name llm-manager --env-file .env --network <network> llm-manager:latest
```
