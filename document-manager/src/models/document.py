import datetime

from sqlmodel import SQLModel, Field
from sqlalchemy import Column, String, LargeBinary


class DocumentBase(SQLModel):
    title: str = Column(String, nullable=False, index=True)
    content: bytes = Column(LargeBinary)


# Had to name it MyDocument because Document is already taken by langchain
class MyDocument(DocumentBase, table=True):
    id: int = Field(default=None, primary_key=True)
    created_at: datetime.time = Field(default=datetime.datetime.now())


class DocumentCreate(DocumentBase):
    pass
