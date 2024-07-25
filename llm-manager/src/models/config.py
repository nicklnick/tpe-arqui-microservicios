from pydantic_settings import BaseSettings

class GeminiSettings(BaseSettings):
    google_api_key: str
    model: str

    class Config:
        env_prefix = "GEMINI_"
        env_file = ".env"

class RabbitMQSettings(BaseSettings):
    host: str
    queue: str

    class Config:
        env_prefix = "RABBITMQ_"
        env_file = ".env"

class PGVectorSettings(BaseSettings):
    host: str
    port: int
    database: str
    user: str
    password: str
    collection_name: str

    class Config:
        env_prefix = "PGVECTOR_"
        env_file = ".env"

class Settings(BaseSettings):
    gemini: GeminiSettings()
    rabbitmq: RabbitMQSettings()
    pgvector: PGVectorSettings()

gemini_settings = GeminiSettings()
rabbitmq_settings = RabbitMQSettings()
pgvector = PGVectorSettings()
settings = Settings(gemini=gemini_settings, rabbitmq=rabbitmq_settings, pgvector=pgvector)