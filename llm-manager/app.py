import json

from faststream import FastStream, apply_types
from faststream.rabbit import RabbitBroker, RabbitMessage

from src.config import RabbitMQSettings
from src.models.InputMessage import InputMessage
from src.models.ResponseMessage import ResponseMessage
from src.dependencies import provide_llm_service
from src.services.LlmService import LlmService


def get_rabbit_broker():
    settings = RabbitMQSettings()
    return RabbitBroker(settings.build_connection_url()), settings


broker, rabbit_settings = get_rabbit_broker()
app = FastStream(broker)

@apply_types
@broker.subscriber(rabbit_settings.input_queue)
@broker.publisher(rabbit_settings.output_queue)
async def handle_msg(msg: RabbitMessage) -> ResponseMessage:

    # Decode the byte string to a regular string
    decoded_body = msg.body.decode('utf-8')
    input_msg = InputMessage(**json.loads(decoded_body))

    service: LlmService = provide_llm_service()
    response: ResponseMessage = await service.process_input(input_msg)

    return response
