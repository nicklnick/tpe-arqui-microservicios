package ar.edu.itba.arquimicro.ampqcontrollers.listeners;


import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.IApiGatewayService;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import ar.edu.itba.arquimicro.services.models.InputResponsePayload;
import ar.edu.itba.arquimicro.services.models.LlmResponse;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class ResponseListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseListener.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final ICacheService cacheService;
    private final IMessageHistoryService messageHistoryService;
    private final IApiGatewayService apiGatewayService;

    public ResponseListener(ICacheService cacheService, IMessageHistoryService messageHistoryService, IApiGatewayService apiGatewayService) {
        this.cacheService = cacheService;
        this.messageHistoryService = messageHistoryService;
        this.apiGatewayService = apiGatewayService;
    }

    // Receive response from LLM Manager
    @RabbitListener(queues = QUEUES_DATA.RECEIVE_LLM)
    public void receiveResponse(String jsonResponse) throws JsonProcessingException {
        // Recibir respuesta
        LOGGER.info("Received response [{}] from [{}] queue", jsonResponse, QUEUES_DATA.RECEIVE_LLM);

        LlmResponse response = mapper.readValue(jsonResponse, LlmResponse.class);

        CacheableMessageData messageData = cacheService.find(response.messageId());

        messageHistoryService.saveMessageToHistory(new MessageToHistory(messageData.chatId(), response.question(), response.answer()));

        apiGatewayService.sendQuestionResponse(new InputResponsePayload(messageData.sessionId(), messageData.chatId(), response.question(), response.answer()));

    }
}
