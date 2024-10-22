package g1.arquimicroservicios.apigw.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.services.contracts.IUsersService;
import g1.arquimicroservicios.apigw.services.implementations.requestsDtos.RegisterRequestDto;
import g1.arquimicroservicios.apigw.services.implementations.requestsDtos.SignInRequestDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Service
public class UsersService implements IUsersService {



    @Value("${role-manager.api}")
    private String url;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UsersService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<Boolean> register(String email, String password, String name, String role)
    {

        RegisterRequestDto dto = new RegisterRequestDto(email,password,role,name);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(dto); // Convert the request body to JSON

            // Create the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/users/register"))  // Assuming the URL is stored in the application properties
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(response.statusCode() == 201);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }


    @Override
    public UserSignInResponseDto signIn(String email, String password)
    {
        SignInRequestDto dto = new SignInRequestDto(email,password);

        try {
            String requestBodyJson = objectMapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/users/signIn"))  // Assuming the URL is stored in the application properties
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200){
                return objectMapper.readValue(response.body(), UserSignInResponseDto.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }
}
