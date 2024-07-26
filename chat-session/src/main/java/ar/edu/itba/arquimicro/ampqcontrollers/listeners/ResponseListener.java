package ar.edu.itba.arquimicro.ampqcontrollers.listeners;


import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import ar.edu.itba.arquimicro.services.models.LlmResponse;
import ar.edu.itba.arquimicro.services.models.Message;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
public class ResponseListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseListener.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private final ICacheService cacheService;
    private final IMessageHistoryService messageHistoryService;
    public ResponseListener( ICacheService cacheService, IMessageHistoryService messageHistoryService) {
        this.cacheService = cacheService;
        this.messageHistoryService = messageHistoryService;
    }

    // Receive response from LLM Manager
    @RabbitListener(queues = QUEUES_DATA.RECEIVE_LLM)
    public void receiveResponse(String jsonResponse) throws JsonProcessingException {
        // Recibir respuesta
        LOGGER.info("Received response [{}] from [{}] queue", jsonResponse , QUEUES_DATA.RECEIVE_LLM);

        LlmResponse response = mapper.readValue(jsonResponse,LlmResponse.class);

        CacheableMessageData messageData = cacheService.find(response.messageId());


        messageHistoryService.saveMessageToHistory(new MessageToHistory(messageData.chatId(), response.question(),response.answer()));

        System.out.println();


//        // Enviar a que se guarde al historial de mensajes
//        LOGGER.info("Sending response [{}] to [{}] queue", response, QUEUES_DATA.SAVE_RESPONSE);
//        final InputResponsePayload payload = new InputResponsePayload(Integer.parseInt(chatId), input, response);
//        rabbitTemplate.convertAndSend(QueueNames.SAVE_RESPONSE, payload);
//
//        // Enviar al api gw
//        LOGGER.info("Sending response [{}] to [{}] queue", response, QueueNames.SEND_RESPONSE_TO_USER);
//        rabbitTemplate.convertAndSend(QueueNames.SEND_RESPONSE_TO_USER, payload);

    }
}
