from sqlmodel import create_engine, SQLModel, Session

from src.config.documents_db_config import settings

# must import the models to create the tables
from src.models import document

engine = create_engine(settings.PG_URL.__str__(), echo=True)


def init_db():
    SQLModel.metadata.create_all(engine)


def get_session():
    with Session(engine) as session:
        yield session
