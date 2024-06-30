from contextlib import asynccontextmanager
from langchain_core.documents import Document

from fastapi import FastAPI, Depends, HTTPException, status, Response

from src.db import init_db, get_session
from src.models.document import MyDocument, DocumentCreate
from src.config.embeddings_db_config import vectorstore


@asynccontextmanager
async def lifespan(app: FastAPI):
    init_db()
    yield


app = FastAPI(lifespan=lifespan)


@app.get("/documents/{document_id}", response_model=MyDocument)
async def get_document(document_id: int, session=Depends(get_session)) -> MyDocument:
    document = session.get(MyDocument, document_id)
    if not document:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Document not found"
        )

    return document


@app.post("/documents")
async def load_document(document: DocumentCreate, session=Depends(get_session)) -> None:
    document = MyDocument(title=document.title, content=document.content)

    # Add the document to the documents database
    session.add(document)
    session.commit()

    # Add the document to the vectorstore
    documents = []
    documents.append(
        Document(
            page_content=document.content,
            metadata={
                "id": document.id,
                "title": document.title,
            },
        )
    )
    vectorstore.add_documents(documents, ids=[document.id])

    return Response(status_code=status.HTTP_201_CREATED)
