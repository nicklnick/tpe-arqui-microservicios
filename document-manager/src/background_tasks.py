from langchain_core.documents import Document
from src.config.embeddings_db_config import vectorstore


def process_document_background(pages, id: int) -> None:
    try:
        documents = []
        for page in pages:
            combined_id = int(f"{id}{page.number}")

            documents.append(
                Document(
                    page_content=page.get_text(),
                    metadata={
                        "id": combined_id,
                        "document_id": id,
                        "page_number": page.number,
                    },
                )
            )

        vectorstore.add_documents(
            documents, ids=[doc.metadata["id"] for doc in documents]
        )
    except Exception as e:
        print("Error processing document")
        raise e
