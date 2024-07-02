from dotenv import load_dotenv
from langchain_cohere import CohereEmbeddings
from langchain_postgres import PGVector
from langchain_postgres.vectorstores import PGVector
from pydantic import PostgresDsn
from pydantic_settings import BaseSettings


class EmbeddingsSettings(BaseSettings):
    PGVECTOR_HOST: str = "localhost"
    PGVECTOR_PORT: int = 5432
    PGVECTOR_DB: str = "db_embeddings"
    PGVECTOR_USER: str = "langchain"
    PGVECTOR_PASSWORD: str = "langchain"
    PGVECTOR_URL: str = ""

    class Config:
        case_sensitive = True


load_dotenv()
embeddings_settings = EmbeddingsSettings()
embeddings_settings.PGVECTOR_URL = PostgresDsn.build(
    scheme="postgresql+psycopg",
    host=embeddings_settings.PGVECTOR_HOST,
    port=embeddings_settings.PGVECTOR_PORT,
    username=embeddings_settings.PGVECTOR_USER,
    password=embeddings_settings.PGVECTOR_PASSWORD,
    path=embeddings_settings.PGVECTOR_DB,
)

# See docker command above to launch a postgres instance with pgvector enabled.
connection = embeddings_settings.PGVECTOR_URL
collection_name = "arqui_docs"
embeddings = CohereEmbeddings()

vectorstore = PGVector(
    embeddings=embeddings,
    collection_name=collection_name,
    connection=embeddings_settings.PGVECTOR_URL.__str__(),
    use_jsonb=True,
)
