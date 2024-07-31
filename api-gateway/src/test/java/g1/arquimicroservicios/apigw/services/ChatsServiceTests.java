package g1.arquimicroservicios.apigw.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.services.implementations.ChatsService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ChatsServiceTests {


    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private ChatsService chatsService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setField(chatsService, "url", "http://localhost:8080");
    }



    @Test
    void testGetUserChatsReturnsEmptyListOn204() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(204);

        List<ChatsServiceResponseDto> result = chatsService.getUserChats(1);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserChatsReturnsChatsOn200() throws Exception {
        String jsonResponse = "[{\"chatId\":1,\"chatName\":\"Chat1\"}]";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        List<ChatsServiceResponseDto> result = chatsService.getUserChats(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getChatId());
        assertEquals("Chat1", result.get(0).getChatName());
    }

    @Test
    void testCreateUserChatReturnsChatIdOn201() throws Exception {
        String jsonResponse = "{\"chatId\":1}";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(201);
        when(httpResponse.body()).thenReturn(jsonResponse);

        ServiceResponse<Integer, HttpStatus> result = chatsService.createUserChat(1, "TestChat");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.httpStatusResponse());
        assertEquals(1, result.value());
    }

    @Test
    void testCreateUserChatReturnsErrorStatus() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);

        ServiceResponse<Integer, HttpStatus> result = chatsService.createUserChat(1, "TestChat");
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.httpStatusResponse());
        assertNull(result.value());
    }
}
