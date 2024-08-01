import io

from fastapi.testclient import TestClient
import fitz

from src.app import app
from src.db import init_db

TEST_DOCUMENT = {"title": "New Document", "content": "This is a new document."}
PDF_BYTES = b"%PDF-1.3\n%\x93\x8c\x8b\x9e ReportLab Generated PDF document http://www.reportlab.com\n1 0 obj\n<<\n/F1 2 0 R\n>>\nendobj\n2 0 obj\n<<\n/BaseFont /Helvetica /Encoding /WinAnsiEncoding /Name /F1 /Subtype /Type1 /Type /Font\n>>\nendobj\n3 0 obj\n<<\n/Contents 7 0 R /MediaBox [ 0 0 612 792 ] /Parent 6 0 R /Resources <<\n/Font 1 0 R /ProcSet [ /PDF /Text /ImageB /ImageC /ImageI ]\n>> /Rotate 0 /Trans <<\n\n>> \n  /Type /Page\n>>\nendobj\n4 0 obj\n<<\n/PageMode /UseNone /Pages 6 0 R /Type /Catalog\n>>\nendobj\n5 0 obj\n<<\n/Author (anonymous) /CreationDate (D:20240731230558+03'00') /Creator (ReportLab PDF Library - www.reportlab.com) /Keywords () /ModDate (D:20240731230558+03'00') /Producer (ReportLab PDF Library - www.reportlab.com) \n  /Subject (unspecified) /Title (untitled) /Trapped /False\n>>\nendobj\n6 0 obj\n<<\n/Count 1 /Kids [ 3 0 R ] /Type /Pages\n>>\nendobj\n7 0 obj\n<<\n/Filter [ /ASCII85Decode /FlateDecode ] /Length 107\n>>\nstream\nGapQh0E=F,0U\\H3T\\pNYT^QKk?tc>IP,;W#U1^23ihPEM_?CW4KISi90MjG^2,FS#<RC5+c,n)Z;(0aDb\"5I[<!^TD#gi]&=5X,[6,OA.~>endstream\nendobj\nxref\n0 8\n0000000000 65535 f \n0000000073 00000 n \n0000000104 00000 n \n0000000211 00000 n \n0000000404 00000 n \n0000000472 00000 n \n0000000768 00000 n \n0000000827 00000 n \ntrailer\n<<\n/ID \n[<17d16fd1efcfdfec61330f04d7d3e581><17d16fd1efcfdfec61330f04d7d3e581>]\n% ReportLab generated PDF document -- digest (http://www.reportlab.com)\n\n/Info 5 0 R\n/Root 4 0 R\n/Size 8\n>>\nstartxref\n1024\n%%EOF\n"


init_db()

client = TestClient(app)


def test_get_document_not_found():
    # Arrange

    # Act
    response = client.get("/api/documents/999")

    # Assert
    assert response.status_code == 404
    assert response.json() == {"detail": "Document not found"}


def test_get_documents_with_0_documents():
    # Arrange

    # Act
    response = client.get("/api/documents")

    # Assert
    assert response.status_code == 204


def test_load_document():
    # Arrange

    # Act
    response = client.post(
        "/api/documents",
        files={"file": ("sample.pdf", io.BytesIO(PDF_BYTES), "application/pdf")},
        data={"title": "Test Document"},
    )

    # Assert
    assert response.status_code == 201
    assert response.headers["Location"] == "/api/documents/1"


def test_get_document():
    # Arrange
    document_id = 1

    # Act
    response = client.get(f"/api/documents/{document_id}")

    # Assert
    assert response.status_code == 200
    assert response.headers["content-type"] == "application/pdf"
    fitz_document = fitz.open(stream=response.content, filetype="pdf")
    assert fitz_document.is_pdf == True
