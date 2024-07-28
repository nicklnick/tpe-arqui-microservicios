package g1.arquimicroservicios.apigw.services.implementations;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;

@Service
public class ChatsService implements IChatsService {

    @Value("${chats.api}")
    private String url;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatsService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    @Override
    public List<ChatsServiceResponseDto> getUserChats(int userId) {
        try {
            // Create the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/chats/" + userId)) // Assuming the URL is stored in the application properties
                    .GET()
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return Collections.emptyList();
            }
            if (response.statusCode() == 200){
                return objectMapper.readValue(response.body(), new TypeReference<>() {});
            }
            return null;//something went wrong
        } catch (Exception e) {
            e.printStackTrace();
            return null;//something went wrong...
        }
    }

    @Override
    public Optional<Boolean> createUserChat(int userId, String chatName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/chats/" + userId + "?chatName=" + chatName))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<?> response = httpClient.send(request,HttpResponse.BodyHandlers.discarding());
            return Optional.of(response.statusCode() == 201);
        } catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    };

}
