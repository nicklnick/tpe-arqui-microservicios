import logging

from fastapi import Depends
from langchain_cohere import CohereEmbeddings
from langchain_postgres import PGVector
from typing import Optional

from src.models.InputLLM import InputLLM
from sqlalchemy.ext.asyncio import create_async_engine
from src.models.config import PGVectorSettings

LOGGER = logging.getLogger(__name__)

class EmbeddingsRepository:


    def __init__(self, host, port, database, user, password, collection_name):
        self.host = host
        self.port = port
        self.database = database
        self.user = user
        self.password = password

        self.connection = f"postgresql+psycopg://{self.user}:{self.password}@{self.host}:{self.port}/{self.database}"

        self.collection_name= collection_name
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