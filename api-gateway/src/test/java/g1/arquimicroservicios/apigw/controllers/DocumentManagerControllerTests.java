package g1.arquimicroservicios.apigw.controllers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.DocumentDataResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

class DocumentManagerControllerTests {

    @Mock
    private IDocumentManagerService documentManagerService;

    @InjectMocks
    private DocumentManagerController documentManagerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(documentManagerController).build();
    }



    @Test
    void testUploadPdfDocument_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/documents").file(file))//que sea multipart automaticamente lo hace un post
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));
    }

    @Test
    void testUploadPdfDocument_InvalidContentType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/api/documents").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only pdf files are allowed"));
    }

    @Test
    void testGetCurrentDocuments_Success() throws Exception {
        List<DocumentDataResponseDto> documentList = List.of(new DocumentDataResponseDto(1, "Document 1"));
        ServiceResponse<List<DocumentDataResponseDto>, HttpStatus> serviceResponse = new ServiceResponse<>(documentList, HttpStatus.OK);

        when(documentManagerService.getData(anyInt(), anyInt())).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/documents").param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Document 1"));
    }

    @Test
    void testGetCurrentDocuments_InvalidPage() throws Exception {
        mockMvc.perform(get("/api/documents").param("page", "0").param("pageSize", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCurrentDocuments_InvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/documents").param("page", "1").param("pageSize", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/documents").param("page", "1").param("pageSize", "21"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSpecificDocument_Success() throws Exception {
        byte[] documentContent = "test content".getBytes();
        ServiceResponse<byte[], HttpStatus> serviceResponse = new ServiceResponse<>(documentContent, HttpStatus.OK);

        when(documentManagerService.getDocument(anyInt())).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().bytes(documentContent));
    }

    @Test
    void testGetSpecificDocument_NotFound() throws Exception {
        ServiceResponse<byte[], HttpStatus> serviceResponse = new ServiceResponse<>(null, HttpStatus.NOT_FOUND);

        when(documentManagerService.getDocument(anyInt())).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isNotFound());
    }
}
