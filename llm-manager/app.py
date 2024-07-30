from faststream import FastStream, apply_types
from faststream.rabbit import RabbitBroker

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
async def handle_msg(msg: InputMessage) -> ResponseMessage:
    service: LlmService = provide_llm_service()
    response: ResponseMessage = await service.process_input(msg)

    return response
