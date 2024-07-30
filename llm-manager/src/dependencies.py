from langchain_core.embeddings import Embeddings
from langchain_core.language_models import BaseChatModel
from langchain_core.vectorstores import VectorStore
from langchain_postgres import PGVector
from sqlalchemy.ext.asyncio import AsyncEngine, create_async_engine

from src.config import PGVectorSettings
from src.constants import PGVECTOR_COLLECTION
from src.externals.GeminiAIModelsProvider import GeminiAIModelsProvider
from src.externals.interfaces import AIModelsProvider

from src.services.LlmService import LlmService


def provide_ai_models_provider() -> AIModelsProvider:
    return GeminiAIModelsProvider()


def provide_embeddings_model(embeddings_provider: AIModelsProvider) -> Embeddings:
    return embeddings_provider.get_embeddings_model()


def provide_chat_model(model_provider: AIModelsProvider) -> BaseChatModel:
    return model_provider.get_chat_model()


def provide_pgvector_engine():
    settings = PGVectorSettings()
    return create_async_engine(settings.build_connection_url(), echo=True)

def provide_vector_store(engine: AsyncEngine,
                         embeddings: Embeddings) -> VectorStore:
     return PGVector(
         embeddings=embeddings,
         connection=engine,
         collection_name=PGVECTOR_COLLECTION
    )

def provide_llm_service() -> LlmService:
    ai_models_provider = provide_ai_models_provider()
    pgvector_engine = provide_pgvector_engine()
    chat_model = provide_chat_model(ai_models_provider)
    embeddings_models = provide_embeddings_model(ai_models_provider)
    vector_store = provide_vector_store(pgvector_engine, embeddings_models)

    return LlmService(chat_model, vector_store)
