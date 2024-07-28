package g1.arquimicroservicios.apigw.controllers;

import g1.arquimicroservicios.apigw.controllers.DocumentManagerController;
import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DocumentManagerController.class)
public class DocumentManagerControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDocumentManagerService documentManagerService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/documents").file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));
    }

    @Test
    public void testUploadNonPdfFile() throws Exception {
        MockMultipartFile nonPdfFile = new MockMultipartFile("file", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/api/documents").file(nonPdfFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only pdf files are allowed"));
    }

    @Test
    public void testUploadFileServiceReturnsEmpty() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "pdf content".getBytes());

        Mockito.when(documentManagerService.uploadFile(Mockito.any(MultipartFile.class))).thenReturn(Optional.empty());

        mockMvc.perform(multipart("/api/documents").file(pdfFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUploadFileServiceReturnsFalse() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "pdf content".getBytes());

        Mockito.when(documentManagerService.uploadFile(Mockito.any(MultipartFile.class))).thenReturn(Optional.of(false));

        mockMvc.perform(multipart("/api/documents").file(pdfFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUploadFileServiceReturnsTrue() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "pdf content".getBytes());

        Mockito.when(documentManagerService.uploadFile(Mockito.any(MultipartFile.class))).thenReturn(Optional.of(true));

        mockMvc.perform(multipart("/api/documents").file(pdfFile))
                .andExpect(status().isCreated());
    }

}
