package ar.edu.itba.arquimicro.ampqcontrollers.listeners;

import ar.edu.itba.arquimicro.ampqcontrollers.payloads.InputRequestPayload;
import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.ILlmService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import ar.edu.itba.arquimicro.services.models.LlmPayload;
import ar.edu.itba.arquimicro.services.models.MessageHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class InputListener {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(InputListener.class);

    private static final int MESSAGES_LIMIT_BACKWARDS = 5;
    private final IMessageHistoryService messageHistoryService;
    private final ICacheService cacheService;
    private final ILlmService llmService;

    public InputListener(IMessageHistoryService messageHistoryService, ICacheService cacheService, ILlmService llmService) {
        this.messageHistoryService = messageHistoryService;
        this.cacheService = cacheService;
        this.llmService = llmService;
    }

    // Receive input from api gw
    // sends data to llm manager,
    @RabbitListener(queues = QUEUES_DATA.PROCESS_INPUT_QUEUE)
    public void receiveInput(String input, String chatId) throws JsonProcessingException {
        // Recibir input
        LOGGER.info("Received [{}] for chat [{}] from [{}] queue", input, chatId, QUEUES_DATA.PROCESS_INPUT_QUEUE);

        //generate message id for llm request, also redis key
        String messageId = generateMessageId();

        //Retrieve payload
        InputRequestPayload inputPayload = mapper.readValue(input, InputRequestPayload.class);

        //get message history
        MessageHistory previousMessages = messageHistoryService.getMessageHistory(inputPayload.chatId(), MESSAGES_LIMIT_BACKWARDS);//

        //cache messageId => SessionId
        cacheService.put(messageId,new CacheableMessageData(inputPayload.sessionId(), inputPayload.chatId()));

        llmService.sendMessageToLlm(new LlmPayload(messageId,inputPayload.question(),previousMessages.messages()));


    }




    private String generateMessageId(){
        return UUID.randomUUID().toString();
    }
}
