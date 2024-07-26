import asyncio
from unittest.mock import patch, AsyncMock
from langchain_core.documents import Document

import app

MOCKED_RESPONSE= "Received a mocked response"

async def mock_ainvoke(template_dict: dict) -> str:
    # Simula el comportamiento del método ainvoke
    return MOCKED_RESPONSE

# Mock para la creación del chain
class MockChain:
    def __init__(self, *args, **kwargs):
        self.parser = None

    def __or__(self, other):
        self.parser = other
        return self

    async def ainvoke(self, template_dict):
        return await mock_ainvoke(template_dict)

with patch('src.models.GeminiLlm.create_stuff_documents_chain', return_value=MockChain()) as MockCreateChain:
    with patch('app.EmbeddingsRepository') as MockRepository:
        mock_repository = MockRepository.return_value
        mock_repository.connect = AsyncMock()
        mock_repository.get_similar_embeddings = AsyncMock()
        mock_repository.get_similar_embeddings.return_value = [
            Document(page_content="Paris, la capital de Francia, es de las mas bellas ciudades"),
            Document(page_content="Buenos Aires, la capital de Argentina, tiene una arquitectura similar a Paris"),
        ]

        asyncio.run(app.main())