from abc import ABC, abstractmethod


class AIModelsProvider(ABC):

    @abstractmethod
    def get_embeddings_model(self):
        pass

    @abstractmethod
    def get_chat_model(self):
        pass