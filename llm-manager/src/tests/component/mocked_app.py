from typing import Callable, Awaitable

from unittest.mock import patch, MagicMock

from aio_pika import IncomingMessage
from fastapi import FastAPI
from fastapi.responses import JSONResponse

from faststream import apply_types
from faststream.rabbit.fastapi import RabbitRouter, RabbitMessage
from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
from langchain_core.language_models import BaseChatModel
from langchain_postgres import PGVector
from sqlalchemy.ext.asyncio import AsyncEngine
import json

from src.config import RabbitMQSettings
from src.externals.interfaces import AIModelsProvider
from src.models.InputMessage import InputMessage
from src.models.ResponseMessage import ResponseMessage
from src.dependencies import provide_llm_service
from src.services.LlmService import LlmService


def configure_mock_provide_vector_store(mock_provide_vector_store):
    mock_pgvector = MagicMock(spec=PGVector)
    mock_retriever = MagicMock()
    mock_retriever.retrieve.return_value = [
        Document(page_content="La capital de Francia es Paris"),
        Document(page_content="Buenos Aires es la capital de Argentina")
    ]

    mock_pgvector.as_retriever.return_value = mock_retriever

    mock_provide_vector_store.return_value = mock_pgvector

async def custom_parser(msg: IncomingMessage, original_parser: Callable[[IncomingMessage], Awaitable[RabbitMessage]],) -> RabbitMessage:
    print(msg)
    print(msg.body)
    parsed_msg = await original_parser(msg)
    return parsed_msg

def configure_mock_provide_pgvector_engine(mock_provide_pgvector_engine):
    mock_engine = MagicMock(spec=AsyncEngine)
    mock_provide_pgvector_engine.return_value = mock_engine

def configure_mock_provide_ai_models_provider(mock_provide_ai_models_provider):
    mock_ai_models_provider = MagicMock(spec=AIModelsProvider)
    mock_embeddings_model = MagicMock(spec=Embeddings)
    mock_chat_model = MagicMock(spec=BaseChatModel)

    mock_ai_models_provider.get_chat_model.return_value = mock_chat_model
    mock_ai_models_provider.get_embeddings_model.return_value = mock_embeddings_model

    mock_provide_ai_models_provider.return_value = mock_ai_models_provider

def get_rabbit_router() -> [RabbitRouter,RabbitMQSettings]:
    settings = RabbitMQSettings()
    return RabbitRouter(settings.build_connection_url(), parser=custom_parser), settings

router, rabbit_settings = get_rabbit_router()

MOCKED_RESPONSE = 'Paris y torre eiffel'

@router.subscriber(rabbit_settings.input_queue)
@router.publisher(rabbit_settings.output_queue)
async def handle_msg(msg: RabbitMessage) -> ResponseMessage:
    with patch('src.dependencies.provide_vector_store') as mock_provide_vector_store, \
            patch('src.dependencies.provide_pgvector_engine') as mock_provide_pgvector_engine:
            #patch('src.dependencies.provide_ai_models_provider') as mock_provide_ai_models_provider, \
            #patch('src.services.LlmService.create_retrieval_chain', autospec=True) as mock_create_chain:

        #mock_rag_chain = AsyncMock()
        #mock_rag_chain.ainvoke.return_value = {'answer': MOCKED_RESPONSE}
        #mock_create_chain.return_value = mock_rag_chain

        print("=============================================================")
        print(msg)
        print("-------------------------------------------------------------")
        print(msg.body)
        print("=============================================================")

        # Decode the byte string to a regular string
        decoded_body = msg.body.decode('utf-8')

        print("---------------------------------------------------")
        print(decoded_body)
        print("---------------------------------------------------")
        
        # Deserialize the JSON string to a Python dictionary
        input_msg = InputMessage(**json.loads(decoded_body))


        configure_mock_provide_vector_store(mock_provide_vector_store)
        configure_mock_provide_pgvector_engine(mock_provide_pgvector_engine)
        #configure_mock_provide_ai_models_provider(mock_provide_ai_models_provider)

        service: LlmService = provide_llm_service()
        response: ResponseMessage = await service.process_input(input_msg)

        return response

@router.after_startup
async def health_set(app: FastAPI):
    global healthy
    healthy= True
@router.get("/health")
async def health():
    global healthy
    if not healthy:
        return JSONResponse(status_code=503,content={"status": "unhealthy"})
    else:
        return JSONResponse(status_code=200,content={"status": "healthy"})

app = FastAPI(lifespan=router.lifespan_context)
app.include_router(router)
