import pytest
import asyncio
from unittest.mock import patch, AsyncMock
import pika
import json
import signal
import sys

from pika.adapters.asyncio_connection import AsyncioConnection
from pika.channel import Channel

from src.models.config import RabbitMQSettings
from pika.connection import Connection
from app import main
from langchain_core.documents import Document

class AsyncRabbitMQPublisher:
    def __init__(self):
        self._connection = None
        self._channel = None
        self.settings = RabbitMQSettings()
        self.loop = asyncio.get_event_loop()
        self.stop_event = asyncio.Event()
        self.callback_queue = "response_queue"

        self.connection_event= None

        self.response = None

    async def connect(self):
        self.connection_event = asyncio.Event()
        self._connection = AsyncioConnection(
            pika.ConnectionParameters(host=self.settings.host),
            on_open_callback=self.on_connection_open,
            custom_ioloop=self.loop
        )
        await self.connection_event.wait()

    def signal_handler(self, sig, frame):
        print('Exiting...')
        if self._connection:
            self._connection.close()
        self.loop.stop()
        sys.exit(0)

    def on_response(self, ch, method, properties, body):
        data = json.loads(body)
        print(f"Input: {data['input']}")
        print(f"Response: {data['response']}")
        self.response = data
        self.stop_event.set()

    def on_connection_open(self, _unused_connection):
        self._connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        self._channel = channel
        self._channel.queue_declare(queue="response_queue", callback=self.on_queue_declareok)
        self.connection_event.set()

    def on_queue_declareok(self, frame):
        self.callback_queue = frame.method.queue
        self._channel.basic_consume(
            queue=self.callback_queue,
            on_message_callback=self.on_response,
            auto_ack=True
        )

        signal.signal(signal.SIGINT, self.signal_handler)


    async def send_message(self, input_message):
        correlation_id = "1"  # Fixed correlation ID for testing
        message_body = json.dumps({"input": input_message})

        print("Publishing message:", message_body)  # Print the message being published
        self._channel.basic_publish(
            exchange='',
            routing_key=self.settings.queue,
            properties=pika.BasicProperties(
                reply_to=self.callback_queue,
                correlation_id=correlation_id
            ),
            body=message_body
        )

    async def wait_for_response(self):
        await self.stop_event.wait()

    async def close(self):
        if self._connection:
            self._connection.close()
        self.stop_event.set()

async def cliente():
    publisher = AsyncRabbitMQPublisher()
    await publisher.connect()

if __name__ == "__main__":
    asyncio.run(cliente())
