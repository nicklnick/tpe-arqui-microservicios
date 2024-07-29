from langchain_google_genai import GoogleGenerativeAIEmbeddings, ChatGoogleGenerativeAI

from src.config import GeminiSettings
from src.constants import GEMINI_CHAT_MODEL, GEMINI_EMBEDDINGS_MODEL
from src.externals.interfaces import AIModelsProvider


class GeminiAIModelsProvider(AIModelsProvider):
    def __init__(self):
        settings = GeminiSettings()
        self.model = ChatGoogleGenerativeAI(google_api_key=settings.google_api_key, model=GEMINI_CHAT_MODEL)
        self.embeddings_model = GoogleGenerativeAIEmbeddings(google_api_key=settings.google_api_key, model=GEMINI_EMBEDDINGS_MODEL)

    def get_embeddings_model(self):
        return self.embeddings_model

    def get_chat_model(self):
        return self.model



