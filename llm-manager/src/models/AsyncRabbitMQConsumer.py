import asyncio

import pika
import logging
from pika.adapters.asyncio_connection import AsyncioConnection

from src.models.config import RabbitMQSettings


LOG_FORMAT = ('%(levelname) -10s %(asctime)s %(name) -30s %(funcName) '
              '-35s %(lineno) -5d: %(message)s')
logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)
LOGGER = logging.getLogger(__name__)

class AsyncRabbitMQConsumer:

    def __init__(self, settings: RabbitMQSettings, on_message_callback):
        self.host = settings.host
        self.queue = settings.queue
        self.connection = None
        self.channel = None

        self.on_message_callback = on_message_callback

    def _on_connection_open(self, _unused_connection):
        LOGGER.info('Connection opened')
        self.connection.channel(on_open_callback=self._on_channel_open)

    def _on_connection_open_error(self, _unused_connection, err):
        LOGGER.error(f'Connection open failed: {err}')
        self.connection.ioloop.stop()

    def _on_connection_closed(self, _unused_connection, reason):
        LOGGER.error(f'Connection closed: {reason}')
        self.connection.ioloop.stop()

    def _on_channel_open(self, channel):
        LOGGER.info('Channel opened')
        self.channel = channel
        self.channel.queue_declare(queue=self.queue, callback=self._on_queue_declared)

    def _on_queue_declared(self, _frame):
        LOGGER.info(f'Queue declared: {self.queue}')
        self.channel.basic_consume(queue=self.queue, on_message_callback=self._on_message)
        LOGGER.info('Started consuming')

    def _on_message(self, channel, method, properties, body):
        asyncio.create_task(self.on_message_callback(body))
        channel.basic_ack(delivery_tag=method.delivery_tag)
        LOGGER.info('Acknowledged message: %s', body)

    async def connect(self):
        loop = asyncio.get_event_loop()
        parameters = pika.ConnectionParameters(host=self.host)
        self.connection = AsyncioConnection(
            parameters,
            on_open_callback=self._on_connection_open,
            on_open_error_callback=self._on_connection_open_error,
            on_close_callback=self._on_connection_closed,
            custom_ioloop=loop
        )
        await asyncio.Future()  # Keep the connection open
