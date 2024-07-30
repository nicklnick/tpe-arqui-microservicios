from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains.history_aware_retriever import create_history_aware_retriever
from langchain.chains.retrieval import create_retrieval_chain
from langchain_core.language_models import BaseChatModel
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.vectorstores import VectorStore

from src.models.InputMessage import InputMessage
from src.models.ResponseMessage import ResponseMessage
from src.utils.PromptUtils import PromptUtils


class LlmService:
    MAX_DOCS_FETCH = 5

    SYSTEM_PROMPT = (
        "Eres un asistente para tareas de pregunta-respuesta."
        "Utiliza los siguientes documentos como contexto para tu respuesta."
        "Si no encuentas la respuesta en el contexto, di que no sabes la respuesta.\n\n"
        "{context}"
    )

    CONTEXT_SYSTEM_PROMPT = (
        "Dado una historia de chat y la ultima pregunta del usuario,"
        "que puede hacer referencia al contexto en la historia de chat,"
        "formula una pregunta unica que pueda ser entendida sin la historia de chat."
        "No respondas la pregunta, solo reformulala si es necesario, y si no dejala como esta."
    )

    def __init__(self, chat_model: BaseChatModel, vector_store: VectorStore):
        self.chat_model = chat_model
        self.vector_store = vector_store

    async def process_input(self, msg: InputMessage) -> ResponseMessage:
        chat_history = PromptUtils.create_chat_message_history(msg.messages)
        retriever = self.vector_store.as_retriever()

        qa_prompt = ChatPromptTemplate.from_messages(
            [
                ("system", self.SYSTEM_PROMPT),
                MessagesPlaceholder("chat_history"),
                ("human", "{input}"),
            ]
        )
        question_answer_chain = create_stuff_documents_chain(self.chat_model, qa_prompt)

        contextualize_q_prompt = ChatPromptTemplate.from_messages(
            [
                ("system", self.CONTEXT_SYSTEM_PROMPT),
                MessagesPlaceholder("chat_history"),
                ("human", "{input}"),
            ]
        )

        history_aware_retriever = create_history_aware_retriever(
            self.chat_model, retriever, contextualize_q_prompt
        )

        rag_chain = create_retrieval_chain(history_aware_retriever, question_answer_chain)

        messages = await chat_history.aget_messages()

        response = (await rag_chain.ainvoke({"input": msg.question, "chat_history": messages}))['answer']

        return ResponseMessage(messageId=msg.messageId, question=msg.question, answer=response)
