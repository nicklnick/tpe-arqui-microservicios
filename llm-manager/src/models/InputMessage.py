from pydantic import BaseModel

from src.models.Message import Message


class InputMessage(BaseModel):
    messageId: str
    question: str
    messages: list[Message]