package g1.arquimicroservicios.apigw.services.implementations;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ReferenceType;
import g1.arquimicroservicios.apigw.services.contracts.IMessageHistoryService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@Service
public class MessageHistoryService implements IMessageHistoryService {


    @Value("${message-history.api}")
    private String url;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int OFFSET_CONSTANT = 5;
    private static final int AMOUNT_PER_PAGE = 5;


    public List<MessageHistoryServiceResponseDto> getMessageHistory(int chatId, int page){

        URI uri = UriComponentsBuilder.fromUriString(url).path("/api/messages/{chatId}").queryParam("offset", page * OFFSET_CONSTANT )
                .queryParam("limit",AMOUNT_PER_PAGE)
                .buildAndExpand(chatId).toUri();

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204){
                return Collections.emptyList();
            }

            if (response.statusCode() == 200){
                return mapper.readValue(response.body(), new TypeReference<>() {
                });
            }

            return null;
        } catch (Exception e){
            return null;
        }

    }
}
