package g1.arquimicroservicios.apigw.services.implementations;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.requestsDtos.CreateChatDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
    public ServiceResponse<Integer, HttpStatus> createUserChat(int userId, String chatName) {
        try {
            String json = objectMapper.writeValueAsString(new CreateChatDto(chatName));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/chats/" + userId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201){
                JsonNode jsonNode = objectMapper.readTree(response.body());
                int chatId =  jsonNode.get("chatId").asInt();
                return new ServiceResponse<>(chatId, HttpStatus.OK);
            }
            return new ServiceResponse<>(null, HttpStatus.valueOf(response.statusCode()));
        } catch (Exception e ){
            e.printStackTrace();
            return new ServiceResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    };

}
