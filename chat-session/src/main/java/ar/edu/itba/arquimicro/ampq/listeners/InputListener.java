package ar.edu.itba.arquimicro.ampq.listeners;

import ar.edu.itba.arquimicro.ampq.payloads.InputRequestPayload;
import ar.edu.itba.arquimicro.ampq.util.QueueNames;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.rest.models.MessageHistory;
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
    private final RestClient restClient;
    private final RabbitTemplate rabbitTemplate;

    private static final int MESSAGES_LIMIT_BACKWARDS = 5;
    private final IMessageHistoryService messageHistoryService;
    private final ICacheService cacheService;

    public InputListener(RestClient restClient, RabbitTemplate rabbitTemplate, IMessageHistoryService messageHistoryService, ICacheService cacheService) {
        this.restClient = restClient;
        this.rabbitTemplate = rabbitTemplate;
        this.messageHistoryService = messageHistoryService;
        this.cacheService = cacheService;
    }

    // Receive input from api gw
    @RabbitListener(queues = QueueNames.PROCESS_INPUT)
    public void receiveInput(String input, String chatId) throws JsonProcessingException {
        // Recibir input
        LOGGER.info("Received [{}] for chat [{}] from [{}] queue", input, chatId, QueueNames.PROCESS_INPUT);

        //generate message id for llm request, also redis key
        String messageId = generateMessageId();


        InputRequestPayload inputPayload = mapper.readValue(input, InputRequestPayload.class);

        MessageHistory previousMessages = messageHistoryService.getMessageHistory(inputPayload.chatId(), MESSAGES_LIMIT_BACKWARDS);//

        cacheService.put(messageId,inputPayload.sessionId());

        System.out.println("");

        // Enviar input y history a la cola de LLM Manager
//        final LlmPayload llmPayload = new LlmPayload(
//                Integer.parseInt(chatId),
//                input,
//                messageHistory.map(MessageHistory::messages).orElse(Collections.emptyList())
//        );

//        LOGGER.info("Sending input [{}] to [{}] queue", input, QueueNames.SEND_LLM);
//        rabbitTemplate.convertAndSend(QueueNames.SEND_LLM, llmPayload);
    }




    private String generateMessageId(){
        return UUID.randomUUID().toString();
    }
}
