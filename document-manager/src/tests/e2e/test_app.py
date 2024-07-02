from fastapi.testclient import TestClient

from src.app import app
from src.db import init_db

TEST_DOCUMENT = {"title": "New Document", "content": "This is a new document."}

init_db()

client = TestClient(app)


def test_get_document_not_found():
    response = client.get("/documents/999")

    assert response.status_code == 404
    assert response.json() == {"detail": "Document not found"}


def test_load_documents():
    response = client.post("/documents/", json=TEST_DOCUMENT)

    assert response.status_code == 201


def test_get_document():
    response = client.get("/documents/1")

    assert response.status_code == 200
    assert response.json().get("id") == 1
    assert response.json().get("title") == TEST_DOCUMENT.get("title")
    assert response.json().get("content") == TEST_DOCUMENT.get("content")
