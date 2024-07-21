package ar.edu.itba.arquimicro.services.implementations;


import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.rest.models.Message;
import ar.edu.itba.arquimicro.services.rest.models.MessageHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;


@Service
public class MessageHistoryService implements IMessageHistoryService {

    private final RestClient restClient;

    @Value("${message-history.api}")
    private String messageHistoryUrl;
    private final String path = "/api/messages/{chatId}";

    public MessageHistoryService(RestClient restClient) {
        this.restClient = restClient;
    }

    public MessageHistory getMessageHistory(int chatId, int limit){
        ResponseEntity<List<Message>> messages = restClient.get().uri(UriComponentsBuilder.fromHttpUrl(messageHistoryUrl).path(path)
                .queryParam("limit",limit)
                .buildAndExpand(chatId).toUri())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        if (messages.getStatusCode().value() == 204){
            return new MessageHistory(chatId, Collections.emptyList());
        }
        return new MessageHistory(chatId,messages.getBody());

    }
}
