package g1.arquimicroservicios.apigw.controllers;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserCreateChatRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserRegisterRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserSignInRequest;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ApiUserSignInResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ChatsResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.MessageHistoryResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.contracts.IMessageHistoryService;
import g1.arquimicroservicios.apigw.services.contracts.IUsersService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

class UserControllerTests {

    @Mock
    private IUsersService userService;

    @Mock
    private IChatsService chatsService;

    @Mock
    private IMessageHistoryService messageHistoryService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testRegister_Success() throws Exception {
        ApiUserRegisterRequest request = new ApiUserRegisterRequest("test@example.com", "password", "Test User", "Student");
        when(userService.register(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(true));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"name\":\"Test User\",\"role\":\"Student\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegister_Failure() throws Exception {
        when(userService.register(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(false));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"name\":\"Test User\",\"role\":\"Student\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegister_Exception() throws Exception {
        when(userService.register(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\",\"name\":\"Test User\",\"role\":\"Student\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSignIn_Success() throws Exception {
        UserSignInResponseDto responseDto = new UserSignInResponseDto(1, "Test User", "test@example.com","Student");
        when(userService.signIn(anyString(), anyString())).thenReturn(responseDto);

        mockMvc.perform(post("/api/users/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("Student"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testSignIn_Failure() throws Exception {
        when(userService.signIn(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/users/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetChatTitles_Success() throws Exception {
        List<ChatsServiceResponseDto> chatsList = List.of(new ChatsServiceResponseDto(1, "Chat 1"));
        when(chatsService.getUserChats(anyInt())).thenReturn(chatsList);

        mockMvc.perform(get("/api/users/1/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value(1))
                .andExpect(jsonPath("$[0].chatName").value("Chat 1"));
    }

    @Test
    void testGetChatTitles_Empty() throws Exception {
        when(chatsService.getUserChats(anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/users/1/chats"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetChatTitles_Error() throws Exception {
        when(chatsService.getUserChats(anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/users/1/chats"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateChat_Success() throws Exception {
        ServiceResponse<Integer, HttpStatus> serviceResponse = new ServiceResponse<>(1, HttpStatus.CREATED);
        when(chatsService.createUserChat(anyInt(), anyString())).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/users/1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chatName\":\"New Chat\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1/chats/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateChat_Error() throws Exception {
        ServiceResponse<Integer, HttpStatus> serviceResponse = new ServiceResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        when(chatsService.createUserChat(anyInt(), anyString())).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/users/1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chatName\":\"New Chat\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetMessageHistory_Success() throws Exception {
        List<MessageHistoryServiceResponseDto> messagesList = List.of(new MessageHistoryServiceResponseDto("Question 1", "Answer 1", 1));
        when(messageHistoryService.getMessageHistory(anyInt(), anyInt())).thenReturn(messagesList);

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Question 1"))
                .andExpect(jsonPath("$[0].answer").value("Answer 1"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetMessageHistory_Empty() throws Exception {
        when(messageHistoryService.getMessageHistory(anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMessageHistory_Error() throws Exception {
        when(messageHistoryService.getMessageHistory(anyInt(), anyInt())).thenReturn(null);

        mockMvc.perform(get("/api/users/1/chats/1")
                        .param("page", "0"))
                .andExpect(status().isInternalServerError());
    }
}
