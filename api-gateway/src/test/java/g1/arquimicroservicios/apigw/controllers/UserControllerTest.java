package g1.arquimicroservicios.apigw.controllers;

import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserRegisterRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserSignInRequest;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ApiUserSignInResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ChatsResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.MessageHistoryResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.contracts.IMessageHistoryService;
import g1.arquimicroservicios.apigw.services.contracts.IUsersService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsersService usersService;

    @MockBean
    private IChatsService chatsService;

    @MockBean
    private IMessageHistoryService messageHistoryService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRegisterUser() throws Exception {

        Mockito.when(usersService.register(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(true));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"name\":\"Test User\", \"role\":\"Student\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testRegisterUserFailure() throws Exception {

        Mockito.when(usersService.register(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(false));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"name\":\"Test User\", \"role\":\"Student\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testRegisterUserServiceError() throws Exception {

        Mockito.when(usersService.register(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"name\":\"Test User\", \"role\":\"Student\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testSignInUser() throws Exception {
        UserSignInResponseDto responseDto = new UserSignInResponseDto(1, "Test User", "test@example.com", "Student");

        Mockito.when(usersService.signIn(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("Student"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    public void testSignInUserFailure() throws Exception {

        Mockito.when(usersService.signIn(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetUserChats() throws Exception {
        ChatsServiceResponseDto chatDto = new ChatsServiceResponseDto(1, "Chat 1");
        List<ChatsServiceResponseDto> chatsList = List.of(chatDto);

        Mockito.when(chatsService.getUserChats(Mockito.anyInt())).thenReturn(chatsList);

        mockMvc.perform(get("/api/users/1/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value(1))
                .andExpect(jsonPath("$[0].chatName").value("Chat 1"));
    }

    @Test
    public void testGetUserChatsEmpty() throws Exception {
        Mockito.when(chatsService.getUserChats(Mockito.anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/1/chats"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateUserChat() throws Exception {
        Mockito.when(chatsService.createUserChat(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(true));

        mockMvc.perform(post("/api/users/1/chats")
                        .param("chatName", "New Chat"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserChatConflict() throws Exception {
        Mockito.when(chatsService.createUserChat(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.of(false));

        mockMvc.perform(post("/api/users/1/chats")
                        .param("chatName", "Existing Chat"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Chat with given name already exists"));
    }

    @Test
    public void testCreateUserChatServiceError() throws Exception {
        Mockito.when(chatsService.createUserChat(Mockito.anyInt(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/1/chats")
                        .param("chatName", "New Chat"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetMessageHistory() throws Exception {
        MessageHistoryServiceResponseDto messageDto = new MessageHistoryServiceResponseDto("Question?", "Answer!", 1);
        List<MessageHistoryServiceResponseDto> messagesList = List.of(messageDto);

        Mockito.when(messageHistoryService.getMessageHistory(Mockito.anyInt(), Mockito.anyInt())).thenReturn(messagesList);

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Question?"))
                .andExpect(jsonPath("$[0].answer").value("Answer!"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testGetMessageHistoryEmpty() throws Exception {
        Mockito.when(messageHistoryService.getMessageHistory(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetMessageHistoryServiceError() throws Exception {
        Mockito.when(messageHistoryService.getMessageHistory(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isInternalServerError());
    }
}
