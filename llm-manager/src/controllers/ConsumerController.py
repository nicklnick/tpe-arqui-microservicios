import asyncio
import json
import pika
import logging

from fastapi import Depends
from pika.adapters.asyncio_connection import AsyncioConnection
from src.models.InputLLM import InputLLM
from pika.exchange_type import ExchangeType

from src.models.config import RabbitMQSettings, PGVectorSettings
from src.repositories.EmbeddingsRepository import EmbeddingsRepository
from src.services.InputService import InputService

LOG_FORMAT = ('%(levelname) -10s %(asctime)s %(name) -30s %(funcName) '
              '-35s %(lineno) -5d: %(message)s')
logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)
LOGGER = logging.getLogger(__name__)

class ConsumerController:
    def __init__(self, host, queue):
        self.host = host
        self.queue = queue
        self.connection = None
        self.channel = None

    async def connect(self):
        loop = asyncio.get_event_loop()
        parameters = pika.ConnectionParameters(host=self.host)
        self.connection = AsyncioConnection(
            parameters,
            on_open_callback=self.on_connection_open,
            on_open_error_callback=self.on_connection_open_error,
            on_close_callback=self.on_connection_closed,
            custom_ioloop=loop
        )
        await asyncio.Future()  # Keep the connection open

    def on_connection_open(self, _unused_connection):
        LOGGER.info('Connection opened')
        self.connection.channel(on_open_callback=self.on_channel_open)

    def on_connection_open_error(self, _unused_connection, err):
        LOGGER.error(f'Connection open failed: {err}')
        self.connection.ioloop.stop()

    def on_connection_closed(self, _unused_connection, reason):
        LOGGER.error(f'Connection closed: {reason}')
        self.connection.ioloop.stop()

    def on_channel_open(self, channel):
        LOGGER.info('Channel opened')
        self.channel = channel
        self.channel.queue_declare(queue=self.queue, callback=self.on_queue_declared)

    def on_queue_declared(self, _frame):
        LOGGER.info(f'Queue declared: {self.queue}')
        self.channel.basic_consume(queue=self.queue, on_message_callback=self.consume)
        LOGGER.info('Started consuming')

    async def process_message(self, body):
        LOGGER.info('Processing message: %s', body)
        data = json.loads(body)
        input_llm = InputLLM(**data)

        pgvector_settings = PGVectorSettings()
        embeddings_repository = EmbeddingsRepository(
            host=pgvector_settings.host,
            port=pgvector_settings.port,
            database=pgvector_settings.database,
            user=pgvector_settings.user,
            password=pgvector_settings.password,
            collection_name=pgvector_settings.collection_name
        )
        input_service = InputService(embeddings_repository)

        await input_service.process_input(input_llm)
        LOGGER.info('Finished processing message: %s', body)

    def consume(self, channel, method, properties, body):
        asyncio.create_task(self.process_message(body))
        channel.basic_ack(delivery_tag=method.delivery_tag)
        LOGGER.info('Acknowledged message: %s', body)