import asyncio
import json
import logging

import pika
from pika.channel import Channel
from pika.spec import BasicProperties
from pika.spec import Basic

from src.exception.exceptions import EmbeddingsDatabaseConnectionException
from src.models.InputLLM import InputLLM
from src.services.ManagerService import ManagerService

LOG_FORMAT = ('%(levelname) -10s %(asctime)s %(name) -30s %(funcName) '
              '-35s %(lineno) -5d: %(message)s')
logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)
LOGGER = logging.getLogger(__name__)

class ConsumerController:

    def __init__(self, input_service: ManagerService):
        self.input_service = input_service

    async def process_message(self, channel: Channel, method: Basic.Deliver, properties: BasicProperties, body):
        data = json.loads(body)
        input_llm = InputLLM(**data)

        LOGGER.info("Managing input")

        try:
            llm_response = await self.input_service.manage_input(input_llm)
        except EmbeddingsDatabaseConnectionException as e:
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=True)
            raise e

        response = {"input": input_llm.input, "response": llm_response}

        LOGGER.info("Response created")

        channel.basic_publish(exchange='',
                              routing_key=properties.reply_to,
                              properties=pika.BasicProperties(correlation_id=properties.correlation_id),
                              body=json.dumps(response))
        channel.basic_ack(delivery_tag=method.delivery_tag)