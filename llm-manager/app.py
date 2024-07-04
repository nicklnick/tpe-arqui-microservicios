import asyncio

from src.controllers.ConsumerController import ConsumerController
from src.models.GeminiLlm import GeminiLlm
from src.models.config import RabbitMQSettings, PGVectorSettings, GeminiSettings
from src.models.AsyncRabbitMQConsumer import AsyncRabbitMQConsumer
from src.repositories.EmbeddingsRepository import EmbeddingsRepository

from src.services.ManagerService import ManagerService


async def handle_message(body):
    embeddings_repository = EmbeddingsRepository(PGVectorSettings())
    gemini = GeminiLlm(GeminiSettings())
    input_service = ManagerService(embeddings_repository, gemini)
    consumer_controller = ConsumerController(input_service)

    await consumer_controller.process_message(body)

async def main():
    rabbitmq_settings = RabbitMQSettings()
    rabbit_mq_consumer = AsyncRabbitMQConsumer(rabbitmq_settings,handle_message)
    await rabbit_mq_consumer.connect()

if __name__ == '__main__':
    asyncio.run(main())