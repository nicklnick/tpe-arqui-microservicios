# Document Management API

This API allows you to manage documents, including listing documents, retrieving a document by its ID, and uploading new documents.

## Endpoints

### Get Documents

Retrieve a list of documents with pagination.

-   **URL**: `/api/documents`
-   **Method**: `GET`
-   **Query Parameters**:
    -   `page` (integer, default: 1): Page number.
    -   `page_size` (integer, default: 10): Number of items per page.
-   **Response**:
    -   **200 OK**: Successful operation with a JSON array of documents.
        ```json
        [
            {
                "id": 1,
                "title": "Document 1"
            },
            {
                "id": 2,
                "title": "Document 2"
            }
        ]
        ```
    -   **204 No Content**: No documents found.

### Upload a Document

Upload a new document.

-   **URL**: `/api/documents`
-   **Method**: `POST`
-   **Form Data**:
    -   `title` (string): Title of the document.
    -   `file` (file): PDF file to be uploaded.
-   **Response**:
    -   **201 Created**: Document successfully uploaded, with the location of the new document in the `Location` header.

### Get Document by ID

Retrieve a document by its ID.

-   **URL**: `/api/documents/{document_id}`
-   **Method**: `GET`
-   **Path Parameters**:
    -   `document_id` (integer): The unique identifier of the document.
-   **Response**:
    -   **200 OK**: Successful operation returning the PDF document.
    -   **404 Not Found**: Document not found.
