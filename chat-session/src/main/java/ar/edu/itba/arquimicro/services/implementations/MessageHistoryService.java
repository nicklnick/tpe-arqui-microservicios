package ar.edu.itba.arquimicro.services.implementations;


import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.Message;
import ar.edu.itba.arquimicro.services.models.MessageHistory;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final ObjectMapper mapper = new ObjectMapper();
    private final RabbitTemplate rabbitTemplate;

    public MessageHistoryService(RestClient restClient, RabbitTemplate template) {
        this.restClient = restClient;
        rabbitTemplate = template;
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


    public void saveMessageToHistory(MessageToHistory message) throws JsonProcessingException {
        String jsonMessage = mapper.writeValueAsString(message);
        rabbitTemplate.convertAndSend(QUEUES_DATA.DIRECT_EXCHANGE_NAME, QUEUES_DATA.SEND_MESSAGE_HISTORY_DATA.SEND_MESSAGE_HISTORY_ROUTING_KEY,jsonMessage);
    }
}
