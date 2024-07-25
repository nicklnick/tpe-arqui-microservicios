from unittest.mock import patch, AsyncMock, MagicMock
from langchain_core.documents.base import Document

import pytest

from src.models.InputLLM import InputLLM
from src.services.ManagerService import ManagerService


@pytest.mark.asyncio
@patch.object(ManagerService, 'get_similar_embeddings', new_callable=AsyncMock)
@patch.object(ManagerService, 'send_query', new_callable=AsyncMock)
async def test_manage_input(mock_send_query, mock_get_similar_embeddings):
    documents = [
            Document(page_content="Paris, la capital de Francia, es de las mas bellas ciudades"),
            Document(page_content="Buenos Aires, la capital de Argentina, tiene una arquitectura similar a Paris"),
        ]
    mock_get_similar_embeddings.return_value = documents

    mock_send_query.return_value = "Mocked Query Response"
    embeddings_repository = MagicMock()
    llm = MagicMock()

    service = ManagerService(embeddings_repository, llm)
    llm_input = InputLLM(input="test_input")
    results_count = 5

    result = await service.manage_input(llm_input)
    mock_get_similar_embeddings.assert_awaited_once_with(llm_input, results_count)
    mock_send_query.assert_awaited_once_with(llm_input, documents)

    assert result == "Mocked Query Response"