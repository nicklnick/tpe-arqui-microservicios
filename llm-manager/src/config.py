from pydantic_settings import BaseSettings


class GeminiSettings(BaseSettings):
    google_api_key: str

    class Config:
        env_prefix = "GEMINI_"
        env_file = ".env"

class RabbitMQSettings(BaseSettings):
    host: str
    username: str
    password: str
    port: int
    input_queue: str
    output_queue: str

    class Config:
        env_prefix = "RABBITMQ_"
        env_file = ".env"

    def build_connection_url(self) -> str:
        return f"amqp://{self.username}:{self.password}@{self.host}:{self.port}/"

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

    def build_connection_url(self) -> str:
        return f"postgresql+psycopg://{self.user}:{self.password}@{self.host}:{self.port}/{self.database}"
