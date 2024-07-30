

from src.models.Message import Message
from langchain_community.chat_message_histories import ChatMessageHistory

class PromptUtils:

    @staticmethod
    def create_chat_message_history(chat_messages: list[Message]) -> ChatMessageHistory:
        chat_history = ChatMessageHistory()
        for message in chat_messages:
            chat_history.add_user_message(message.question)
            chat_history.add_ai_message(message.answer)

        return chat_history