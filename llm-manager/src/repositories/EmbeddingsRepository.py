import asyncio
import logging

from sqlalchemy.exc import OperationalError
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_postgres import PGVector
from typing import Optional

from src.exception.exceptions import EmbeddingsDatabaseConnectionException
from src.models.InputLLM import InputLLM
from sqlalchemy.ext.asyncio import create_async_engine

from src.models.config import PGVectorSettings, GeminiSettings

LOGGER = logging.getLogger(__name__)

class EmbeddingsRepository:


    def __init__(self, pgvector_settings: PGVectorSettings, gemini_settings: GeminiSettings):
        self.host = pgvector_settings.host
        self.port = pgvector_settings.port
        self.database = pgvector_settings.database
        self.user = pgvector_settings.user
        self.password = pgvector_settings.password

        self.connection = f"postgresql+psycopg://{self.user}:{self.password}@{self.host}:{self.port}/{self.database}"

        self.collection_name= pgvector_settings.collection_name
        self.vectorstore : Optional[PGVector] = None

        self.gemini_settings = gemini_settings


    def connect(self):
        engine = create_async_engine(self.connection)
        embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001",google_api_key=self.gemini_settings.google_api_key)

        self.vectorstore = PGVector(
            embeddings=embeddings,
            collection_name=self.collection_name,
            connection= engine,
            use_jsonb= True,
            async_mode=True
        )

    async def get_similar_embeddings(self, input_llm: InputLLM, results_count):
        attempt = 0
        retries = 3
        while attempt < retries:
            try:
                docs = await self.vectorstore.asimilarity_search(input_llm.input, k=results_count)
                return docs
            except OperationalError:
                attempt +=1
                wait_time = 2**attempt
                LOGGER.info(
                    f"Failed to connect to the database (attempt {attempt}/{retries}). Retrying in {wait_time:.2f} seconds...")
                await asyncio.sleep(wait_time)
            except Exception as e:
                LOGGER.error(f"Unexpected error: {e}")
                raise e
        raise EmbeddingsDatabaseConnectionException("Could not connect to the database after multiple retries")