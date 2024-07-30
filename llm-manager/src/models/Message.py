from pydantic import BaseModel


class Message(BaseModel):
    id: int
    question: str
    answer: str
