import asyncio
import functools
import signal

import pika
import logging
from pika.adapters.asyncio_connection import AsyncioConnection

from src.exception.exceptions import UnrecoverableException
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
        self.consumer_tag = None
        self.loop = None

        self.event = None
        self.unacknowledged_delivery_tags = []

        self.on_message_callback = on_message_callback

        self.closing = False
        self.lock =asyncio.Lock()
        self.pending_tasks = set()

    def _on_connection_open(self, _unused_connection):
        LOGGER.info('Connection opened')
        self.connection.channel(on_open_callback=self._on_channel_open)

    def _on_connection_open_error(self, _unused_connection, err):
        LOGGER.error(f'Connection open failed: {err}')
        self.event.set()

    def _on_connection_closed(self, _unused_connection, reason):
        LOGGER.error(f'Connection closed: {reason}')
        self.connection.ioloop.stop()

    def _on_channel_open(self, channel):
        LOGGER.info('Channel opened')
        self.channel = channel
        self.channel.queue_declare(queue=self.queue, callback=self._on_queue_declared)

    def _on_queue_declared(self, _frame):
        LOGGER.info(f'Queue declared: {self.queue}')
        self.consumer_tag = self.channel.basic_consume(queue=self.queue, on_message_callback=self._on_message)
        LOGGER.info('Started consuming')

    def _on_message(self, channel, method, properties, body):
        task = asyncio.create_task(self.on_message_callback(channel, method, properties, body))
        task.name = f"task_{method.delivery_tag}"
        self.pending_tasks.add(task)
        task.add_done_callback(
            lambda t: asyncio.create_task(
                self._on_message_processed(channel, method.delivery_tag, t)
            )
        )
    async def _on_message_processed(self, ch, delivery_tag, task):
        try:
            if task.cancelled():
                LOGGER.error("Task was cancelled")
            else:
                task.result()
        except UnrecoverableException as e:
            LOGGER.error(f"Unrecoverable exception: {e}")
            await self.cancel_channel()
        except asyncio.CancelledError:
            LOGGER.error(f"Task {task.name} was cancelled")
        finally:
            self.pending_tasks.discard(task)

    async def cancel_channel(self):
        await self.lock.acquire()
        if not self.closing:
            self.closing = True
            self.lock.release()

            for task in self.pending_tasks:
                task.cancel()
            for task in self.pending_tasks:
                try:
                    await task
                except asyncio.CancelledError:
                    LOGGER.info(f"Task {task.name} was cancelled successfully")
                except Exception as e:
                    LOGGER.error(f"Cancelled task had an error: {e}")

            cb = functools.partial(self._on_cancel_channel_ok)
            self.channel.basic_cancel(consumer_tag=self.consumer_tag,
                                      callback=cb)
    def _on_cancel_channel_ok(self, method_frame):
        self.channel.close()
        self.connection.close()
        self.event.set()

    async def _shutdown(self):
        await self.cancel_channel()

    def _signal_handler(self, sig):
        LOGGER.info(f"Received signal: {sig.name}")
        asyncio.create_task(self._shutdown())

    async def connect(self):
        self.loop = asyncio.get_event_loop()
        parameters = pika.ConnectionParameters(host=self.host)
        self.connection = AsyncioConnection(
            parameters,
            on_open_callback=self._on_connection_open,
            on_open_error_callback=self._on_connection_open_error,
            on_close_callback=self._on_connection_closed,
            custom_ioloop=self.loop
        )
        self.loop.add_signal_handler(signal.SIGINT,self._signal_handler,signal.SIGINT)
        self.loop.add_signal_handler(signal.SIGTERM, self._signal_handler, signal.SIGTERM)
        self.event = asyncio.Event()
        await self.event.wait()
