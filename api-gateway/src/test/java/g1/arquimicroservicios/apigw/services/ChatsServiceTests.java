package g1.arquimicroservicios.apigw.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.implementations.ChatsService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatsServiceTest {

    private IChatsService chatsService;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        objectMapper = new ObjectMapper();
        chatsService = new ChatsService(httpClient);
        try {
            // Use reflection to set the private url field
            Field urlField = ChatsService.class.getDeclaredField("url");
            urlField.setAccessible(true);
            urlField.set(chatsService, "http://someUrl"); // Mock URL
        } catch (Exception e) {
            System.out.println("Could not modify url endpoint via reflection");
        }
    }

    @Test
    void testGetUserChats_Success() throws Exception {
        int userId = 1;
        String responseBody = "[{\"chatId\":1,\"chatName\":\"Chat 1\"}]";
        @SuppressWarnings("unchecked")
        HttpResponse<String> httpResponse = (HttpResponse<String>) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ChatsServiceResponseDto> chats = chatsService.getUserChats(userId);

        assertNotNull(chats);
        assertFalse(chats.isEmpty());
        assertEquals(1, chats.size());
        assertEquals(1, chats.get(0).getChatId());
        assertEquals("Chat 1", chats.get(0).getChatName());
    }

    @Test
    void testGetUserChats_NoContent() throws Exception {
        int userId = 1;

        @SuppressWarnings("unchecked")
        HttpResponse<String> httpResponse = (HttpResponse<String>) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(204);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ChatsServiceResponseDto> chats = chatsService.getUserChats(userId);

        assertNotNull(chats);
        assertTrue(chats.isEmpty());
    }

    @Test
    void testGetUserChats_Error() throws Exception {
        int userId = 1;
        @SuppressWarnings("unchecked")
        HttpResponse<String> httpResponse = (HttpResponse<String>) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ChatsServiceResponseDto> chats = chatsService.getUserChats(userId);

        assertNull(chats);
    }

    @Test
    void testGetUserChats_Exception() throws Exception {
        int userId = 1;
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        List<ChatsServiceResponseDto> chats = chatsService.getUserChats(userId);

        assertNull(chats);
    }

    @Test
    void testCreateUserChat_Success() throws Exception {
        int userId = 1;
        String chatName = "NewChat";
        @SuppressWarnings("unchecked")
        HttpResponse<Void> httpResponse = (HttpResponse<Void> ) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(201);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenReturn(httpResponse);

        Optional<Boolean> result = chatsService.createUserChat(userId, chatName);

        assertTrue(result.isPresent());
        assertTrue(result.get());
    }

    @Test
    void testCreateUserChat_Conflict() throws Exception {
        int userId = 1;
        String chatName = "Existing Chat";
        @SuppressWarnings("unchecked")
        HttpResponse<Void> httpResponse = (HttpResponse<Void>) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(409);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenReturn(httpResponse);

        Optional<Boolean> result = chatsService.createUserChat(userId, chatName);

        assertTrue(result.isPresent());
        assertFalse(result.get());
    }

    @Test
    void testCreateUserChat_ServiceError() throws Exception {
        int userId = 1;
        String chatName = "New Chat";
        @SuppressWarnings("unchecked")
        HttpResponse<Void> httpResponse = (HttpResponse<Void>) mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenReturn(httpResponse);

        Optional<Boolean> result = chatsService.createUserChat(userId, chatName);

        //the microservice is not available
        assertTrue(result.isPresent());
        assertFalse(result.get());
    }

    @Test
    void testCreateUserChat_Exception() throws Exception {
        int userId = 1;
        String chatName = "New Chat";
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenThrow(new IOException("Network error"));

        Optional<Boolean> result = chatsService.createUserChat(userId, chatName);

        assertFalse(result.isPresent());
    }
}
