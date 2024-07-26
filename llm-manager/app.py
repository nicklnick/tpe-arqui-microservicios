import asyncio
import logging

from src.controllers.ConsumerController import ConsumerController
from src.models.GeminiLlm import GeminiLlm
from src.models.config import RabbitMQSettings, PGVectorSettings, GeminiSettings
from src.models.AsyncRabbitMQConsumer import AsyncRabbitMQConsumer
from src.repositories.EmbeddingsRepository import EmbeddingsRepository

from src.services.ManagerService import ManagerService

LOG_FORMAT = ('%(levelname) -10s %(asctime)s %(name) -30s %(funcName) '
              '-35s %(lineno) -5d: %(message)s')
logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)
LOGGER = logging.getLogger(__name__)


async def handle_message(channel, method, properties, body):
    embeddings_repository = EmbeddingsRepository(PGVectorSettings(), GeminiSettings())
    gemini = GeminiLlm(GeminiSettings())
    input_service = ManagerService(embeddings_repository, gemini)
    consumer_controller = ConsumerController(input_service)

    LOGGER.info("Processing Message...")

    await consumer_controller.process_message(channel, method, properties, body)

async def main():
    rabbitmq_settings = RabbitMQSettings()
    rabbit_mq_consumer = AsyncRabbitMQConsumer(rabbitmq_settings,handle_message)
    await rabbit_mq_consumer.connect()

if __name__ == '__main__':
    asyncio.run(main())