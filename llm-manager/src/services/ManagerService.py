import asyncio
import logging

from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.output_parsers import StrOutputParser
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.documents.base import Document

from src.models.GeminiLlm import GeminiLlm
from src.models.InputLLM import InputLLM
from src.models.config import GeminiSettings
from src.repositories.EmbeddingsRepository import EmbeddingsRepository

LOGGER = logging.getLogger(__name__)

SYSTEM_PROMPT = "Busca la respuesta a [PREGUNTA] en los [DOCUMENTOS] que te envio.Si no la encuentras di que \
no hay informacion suficiente\n [DOCUMENTOS] {context}\n [PREGUNTA]: {input}"

RESULTS_COUNT = 5
class ManagerService:
    def __init__(self, embeddings_repository: EmbeddingsRepository, llm: GeminiLlm):
        self.embeddings_repository = embeddings_repository
        self.llm = llm

    async def send_query(self, input_llm: InputLLM, embeddings: list[Document]):
        LOGGER.info("Connecting to external LLM")
        await self.llm.connect()

        prompt_dict = {"context": embeddings, "input": input_llm.input}
        LOGGER.info("Sending message...")
        return await self.llm.send_message(SYSTEM_PROMPT, prompt_dict)

    async def get_similar_embeddings(self, input_llm: InputLLM, results_count: int):
        self.embeddings_repository.connect()
        return  await self.embeddings_repository.get_similar_embeddings(input_llm, results_count)

    async def manage_input(self, input_llm: InputLLM):
        LOGGER.info("Getting similar documents")
        documents = await self.get_similar_embeddings(input_llm, RESULTS_COUNT)
        if len(documents) < 1:
            return LOGGER.info(f"No hay documentos asociados a:\n[PREGUNTA]: {input_llm.input}")

        return await self.send_query(input_llm, documents)