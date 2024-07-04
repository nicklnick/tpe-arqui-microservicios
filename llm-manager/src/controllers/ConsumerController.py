import json

from src.models.InputLLM import InputLLM
from src.services.ManagerService import ManagerService


class ConsumerController:

    def __init__(self, input_service: ManagerService):
        self.input_service = input_service

    async def process_message(self, body):
        data = json.loads(body)
        input_llm = InputLLM(**data)

        await self.input_service.manage_input(input_llm)