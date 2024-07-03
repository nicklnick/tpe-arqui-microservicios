import io

from fastapi.testclient import TestClient
import fitz

from src.app import app
from src.db import init_db

TEST_DOCUMENT = {"title": "New Document", "content": "This is a new document."}

init_db()

client = TestClient(app)


def test_get_document_not_found():
    # Arrange

    # Act
    response = client.get("/documents/999")

    # Assert
    assert response.status_code == 404
    assert response.json() == {"detail": "Document not found"}


def test_load_document():
    # Arrange
    with open("/usr/app/src/tests/e2e/sample.pdf", "rb") as f:
        file_content = f.read()

    # Act
    response = client.post(
        "/documents",
        files={"file": ("sample.pdf", io.BytesIO(file_content), "application/pdf")},
        data={"title": "Test Document"},
    )

    # Assert
    assert response.status_code == 201


def test_get_document():
    # Arrange
    document_id = 1

    # Act
    response = client.get(f"/documents/{document_id}")

    # Assert
    assert response.status_code == 200
    assert response.headers["content-type"] == "application/pdf"
    fitz_document = fitz.open(stream=response.content, filetype="pdf")
    assert fitz_document.is_pdf == True
