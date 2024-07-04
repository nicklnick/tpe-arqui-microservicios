import logging

from langchain_cohere import CohereEmbeddings
from langchain_postgres import PGVector
from typing import Optional

from src.models.InputLLM import InputLLM
from sqlalchemy.ext.asyncio import create_async_engine

from src.models.config import PGVectorSettings

LOGGER = logging.getLogger(__name__)

class EmbeddingsRepository:


    def __init__(self, pgvector_settings: PGVectorSettings):
        self.host = pgvector_settings.host
        self.port = pgvector_settings.port
        self.database = pgvector_settings.database
        self.user = pgvector_settings.user
        self.password = pgvector_settings.password

        self.connection = f"postgresql+psycopg://{self.user}:{self.password}@{self.host}:{self.port}/{self.database}"

        self.collection_name= pgvector_settings.collection_name
        self.vectorstore : Optional[PGVector] = None


    async def connect(self):
        engine = create_async_engine(self.connection)
        embeddings = await CohereEmbeddings()

        self.vectorstore = PGVector(
            embeddings=embeddings,
            collection_name=self.collection_name,
            connection= engine,
            use_jsonb= True,
            async_mode=True
        )


    async def get_similar_embeddings(self, input_llm: InputLLM, results_count):
        return await self.vectorstore.asimilarity_search(input_llm.input, k=results_count)