from pydantic import BaseModel

class InputLLM(BaseModel):
    input: str