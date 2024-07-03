import asyncio
import logging

from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.output_parsers import StrOutputParser
from langchain_google_genai import ChatGoogleGenerativeAI

from src.models.InputLLM import InputLLM
from src.models.config import GeminiSettings
from src.repositories.EmbeddingsRepository import EmbeddingsRepository

LOGGER = logging.getLogger(__name__)

class InputService:
    def __init__(self, embeddings_repository: EmbeddingsRepository):
        self.embeddings_repository = embeddings_repository
        self.results_count = 10

    async def process_input(self, input_llm: InputLLM):
        LOGGER.info(f'Searching {self.results_count} similar documents to : {input_llm.input}')
        #documents = await self.embeddings_repository.get_similar_embeddings(input_llm, self.results_count)
        LOGGER.info(f'Received documents: {input_llm.input}')

        gemini_settings = GeminiSettings()
        llm = ChatGoogleGenerativeAI(model=gemini_settings.model, google_api_key=gemini_settings.google_api_key)
        parser = StrOutputParser()
        chain = llm | parser

        messages = [SystemMessage(
            content="You are a translator from spanish to german"
        ),
        HumanMessage(
            content=input_llm.input
        )]
        LOGGER.info(f'Sending query...')
        result = await chain.ainvoke(messages)
        LOGGER.info(f'Received response: {result}')