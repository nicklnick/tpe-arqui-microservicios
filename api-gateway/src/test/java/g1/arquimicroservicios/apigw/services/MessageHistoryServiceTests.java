package g1.arquimicroservicios.apigw.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.services.implementations.MessageHistoryService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

class MessageHistoryServiceTests {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private MessageHistoryService messageHistoryService;

    @Mock
    private HttpResponse<String> httpResponse;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageHistoryService = new MessageHistoryService(httpClient);
        ReflectionTestUtils.setField(messageHistoryService, "url", "http://localhost:8080");
    }

    @Test
    void testGetMessageHistory_Success() throws Exception {
        int chatId = 1;
        int page = 0;
        List<MessageHistoryServiceResponseDto> expectedMessages = Collections.singletonList(new MessageHistoryServiceResponseDto());
        String jsonResponse = mapper.writeValueAsString(expectedMessages);

        URI uri = new URI("http://localhost:8080/api/messages/1?offset=0&limit=5");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        List<MessageHistoryServiceResponseDto> response = messageHistoryService.getMessageHistory(chatId, page);

        assertNotNull(response);
        assertEquals(expectedMessages.size(), response.size());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testGetMessageHistory_NoContent() throws Exception {
        int chatId = 1;
        int page = 0;

        URI uri = new URI("http://localhost:8080/api/messages/1?offset=0&limit=5");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(204);

        List<MessageHistoryServiceResponseDto> response = messageHistoryService.getMessageHistory(chatId, page);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testGetMessageHistory_Error() throws Exception {
        int chatId = 1;
        int page = 0;

        URI uri = new URI("http://localhost:8080/api/messages/1?offset=0&limit=5");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        List<MessageHistoryServiceResponseDto> response = messageHistoryService.getMessageHistory(chatId, page);

        assertNull(response);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testGetMessageHistory_Exception() throws Exception {
        int chatId = 1;
        int page = 0;

        URI uri = new URI("http://localhost:8080/api/messages/1?offset=0&limit=5");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        List<MessageHistoryServiceResponseDto> response = messageHistoryService.getMessageHistory(chatId, page);

        assertNull(response);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
