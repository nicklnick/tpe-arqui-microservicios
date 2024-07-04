from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains.question_answering import load_qa_chain
from langchain_core.messages import HumanMessage, SystemMessage
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import PromptTemplate, ChatPromptTemplate
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.documents.base import Document

from src.models.config import GeminiSettings


class GeminiLlm:

    def __init__(self, settings: GeminiSettings):
        self.settings = settings
        self.parser = StrOutputParser()
        self.llm = None

    async def connect(self):
        llm = ChatGoogleGenerativeAI(
            model=self.settings.model,
            google_api_key=self.settings.google_api_key)
        self.llm = llm

    async def send_message(self, prompt_template_str: str , template_dict: dict) -> str:

        prompt_template = ChatPromptTemplate.from_template(prompt_template_str)
        chain = create_stuff_documents_chain(self.llm, prompt_template) | self.parser

        return await chain.ainvoke(template_dict)