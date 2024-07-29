from pydantic import BaseModel


class ResponseMessage(BaseModel):
    messageId: str
    question: str
    answer: str