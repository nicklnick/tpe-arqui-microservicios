from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter


from src.config.embeddings_db_config import vectorstore


def process_document_background(pages, id: int) -> None:
    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=700, chunk_overlap=100, length_function=len, is_separator_regex=False
    )

    try:
        documents = []
        for page in pages:
            documents.append(
                Document(
                    page_content=page.get_text(),
                    metadata={
                        "document_id": id,
                        "page_number": page.number,
                    },
                )
            )

        split_documents = text_splitter.split_documents(documents)

        vectorstore.add_documents(split_documents)
    except Exception as e:
        print("Error processing document")
        raise e
