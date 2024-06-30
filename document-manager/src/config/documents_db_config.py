from dotenv import load_dotenv
from pydantic import PostgresDsn
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    PG_HOST: str = "localhost"
    PG_PORT: int = 5432
    PG_DB: str = "db_document_manager"
    PG_USER: str = "postgres"
    PG_PASSWORD: str = "postgres"
    PG_URL: str = ""

    class Config:
        case_sensitive = True


load_dotenv()
settings = Settings()
settings.PG_URL = PostgresDsn.build(
    scheme="postgresql",
    host=settings.PG_HOST,
    port=settings.PG_PORT,
    username=settings.PG_USER,
    password=settings.PG_PASSWORD,
    path=settings.PG_DB,
)
