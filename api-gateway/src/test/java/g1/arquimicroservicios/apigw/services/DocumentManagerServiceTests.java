package g1.arquimicroservicios.apigw.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import g1.arquimicroservicios.apigw.services.implementations.DocumentManagerService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.DocumentDataResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class DocumentManagerServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DocumentManagerService documentManagerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentManagerService = new DocumentManagerService(restTemplate);
        setField(documentManagerService, "url", "http://localhost:8080");
    }

    @Test
    void testUploadFile_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("file content".getBytes());
        when(file.getOriginalFilename()).thenReturn("test.txt");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(headers, HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class))).thenReturn(responseEntity);
        headers.add("Location", "http://localhost:8080/api/documents/123");

        ServiceResponse<Integer, HttpStatus> response = documentManagerService.uploadFile(file);

        assertNotNull(response);
        assertEquals(123, response.value());
        assertEquals(HttpStatus.CREATED, response.httpStatusResponse());
    }

    @Test
    void testUploadFile_ClientError() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("file content".getBytes());
        when(file.getOriginalFilename()).thenReturn("test.txt");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ServiceResponse<Integer, HttpStatus> response = documentManagerService.uploadFile(file);

        assertNull(response.value());
        assertEquals(HttpStatus.BAD_REQUEST, response.httpStatusResponse());
    }

    @Test
    void testGetData_Success() {
        ResponseEntity<List<DocumentDataResponseDto>> responseEntity = new ResponseEntity<>(
                Collections.singletonList(new DocumentDataResponseDto(1,"first document")), HttpStatus.OK);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), eq(null), ArgumentMatchers.<ParameterizedTypeReference<List<DocumentDataResponseDto>>>any()))
                .thenReturn(responseEntity);

        ServiceResponse<List<DocumentDataResponseDto>, HttpStatus> response = documentManagerService.getData(1, 10);

        assertNotNull(response);
        assertEquals(1, response.value().size());
        assertEquals(HttpStatus.OK, response.httpStatusResponse());
    }

    @Test
    void testGetData_NoContent() {
        ResponseEntity<List<DocumentDataResponseDto>> responseEntity = new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), eq(null), ArgumentMatchers.<ParameterizedTypeReference<List<DocumentDataResponseDto>>>any()))
                .thenReturn(responseEntity);

        ServiceResponse<List<DocumentDataResponseDto>, HttpStatus> response = documentManagerService.getData(1, 10);

        assertNotNull(response);
        assertTrue(response.value().isEmpty());
        assertEquals(HttpStatus.NO_CONTENT, response.httpStatusResponse());
    }


}
