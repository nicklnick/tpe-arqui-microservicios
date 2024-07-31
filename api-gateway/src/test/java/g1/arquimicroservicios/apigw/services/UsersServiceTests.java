package g1.arquimicroservicios.apigw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.services.implementations.UsersService;
import g1.arquimicroservicios.apigw.services.implementations.requestsDtos.RegisterRequestDto;
import g1.arquimicroservicios.apigw.services.implementations.requestsDtos.SignInRequestDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

class UsersServiceTests {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private UsersService usersService;

    @Mock
    private HttpResponse<String> httpResponse;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usersService = new UsersService(httpClient);
        ReflectionTestUtils.setField(usersService, "url", "http://localhost:8080");
    }

    @Test
    void testRegister_Success() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";
        String role = "Student";


        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(201);

        Optional<Boolean> response = usersService.register(email, password, name, role);

        assertTrue(response.isPresent());
        assertTrue(response.get());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testRegister_Failure() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";
        String role = "Student";


        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(400);

        Optional<Boolean> response = usersService.register(email, password, name, role);

        assertTrue(response.isPresent());
        assertFalse(response.get());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testRegister_Exception() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";
        String role = "Student";

        RegisterRequestDto dto = new RegisterRequestDto(email, password, role, name);
        String requestBodyJson = objectMapper.writeValueAsString(dto);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        Optional<Boolean> response = usersService.register(email, password, name, role);

        assertFalse(response.isPresent());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }


    @Test
    void testSignIn_Success() throws Exception {
        String email = "test@example.com";
        String password = "password";


        UserSignInResponseDto expectedResponse = new UserSignInResponseDto(1,"Test User",email,"Student");
        String jsonResponse = objectMapper.writeValueAsString(expectedResponse);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);

        UserSignInResponseDto response = usersService.signIn(email, password);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testSignIn_Failure() throws Exception {
        String email = "test@example.com";
        String password = "password";


        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(401);

        UserSignInResponseDto response = usersService.signIn(email, password);

        assertNull(response);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testSignIn_Exception() throws Exception {
        String email = "test@example.com";
        String password = "password";


        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        UserSignInResponseDto response = usersService.signIn(email, password);

        assertNull(response);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
