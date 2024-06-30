import datetime

from sqlmodel import SQLModel, Field


class DocumentBase(SQLModel):
    title: str
    content: bytes


# Had to name it MyDocument because Document is already taken by langchain
class MyDocument(DocumentBase, table=True):
    id: int = Field(default=None, primary_key=True)
    created_at: datetime.time = Field(default=datetime.datetime.now())


class DocumentCreate(DocumentBase):
    pass
