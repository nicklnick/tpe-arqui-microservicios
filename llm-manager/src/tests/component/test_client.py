import json
import pytest
import aio_pika

from src.config import RabbitMQSettings
from src.models.ResponseMessage import ResponseMessage
from src.tests.component.mocked_app import MOCKED_RESPONSE


QUESTION = "¿Cual es la capital de francia y cual es su monumento mas famoso?"
MESSAGED_ID = "1"


@pytest.mark.asyncio
async def test_handle_msg():
    settings = RabbitMQSettings()
    connection = await aio_pika.connect_robust(
        settings.build_connection_url()
    )
    async with connection:
        routing_key = settings.input_queue
        channel = await connection.channel()

        history_message={"id": 1, "question": "¿Cual es el monumento mas famoso de Paris?", "answer": "La torre eiffel"}

        message = {
            "messageId": MESSAGED_ID,
            "question": QUESTION,
            "messages": [history_message]
        }

        await channel.default_exchange.publish(
            aio_pika.Message(body=json.dumps(message).encode()),
            routing_key=routing_key
        )

        queue = await channel.declare_queue(settings.output_queue, auto_delete=True)
        async with queue.iterator() as queue_iter:
            message = await queue_iter.__anext__()
            async with message.process():
                response = ResponseMessage.parse_raw(message.body)

                assert response.question == QUESTION
                assert response.answer == MOCKED_RESPONSE
                assert response.messageId ==  MESSAGED_ID

