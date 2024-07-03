import asyncio

from src.controllers.ConsumerController import ConsumerController
from src.models.config import RabbitMQSettings


async def main():
    rabbitmq_settings = RabbitMQSettings()
    consumer_controller = ConsumerController(rabbitmq_settings.host, rabbitmq_settings.queue)
    await consumer_controller.connect()

if __name__ == '__main__':
    asyncio.run(main())