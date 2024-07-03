from contextlib import asynccontextmanager
from fastapi import (
    FastAPI,
    Depends,
    HTTPException,
    status,
    Response,
    File,
    UploadFile,
    Form,
    BackgroundTasks,
)
import fitz

from langchain_core.documents import Document

from src.db import init_db, get_session
from src.models.document import MyDocument
from src.config.embeddings_db_config import vectorstore
from src.background_tasks import process_document_background


@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    yield


app = FastAPI(lifespan=lifespan)


@app.get("/documents/{document_id}")
async def get_document(document_id: int, session=Depends(get_session)):
    document = session.get(MyDocument, document_id)
    if not document:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Document not found"
        )

    doc = fitz.open(stream=document.content, filetype="pdf")

    return Response(content=doc.write(), media_type="application/pdf", status_code=200)


@app.post("/documents")
async def load_document(
    background_tasks: BackgroundTasks,
    title: str = Form(...),
    file: UploadFile = File(...),
    session=Depends(get_session),
) -> None:
    # Use PyMuPDF to extract text from the PDF
    stream = file.file.read()
    pages = fitz.open(stream=stream, filetype="pdf")

    document = MyDocument(title=title, content=stream)

    # Add the document to the documents database
    session.add(document)
    session.commit()

    background_tasks.add_task(process_document_background, pages, document.id)

    return Response(status_code=status.HTTP_201_CREATED)
