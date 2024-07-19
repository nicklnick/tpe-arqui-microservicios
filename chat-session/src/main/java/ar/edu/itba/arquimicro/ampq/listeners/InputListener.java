package ar.edu.itba.arquimicro.ampq.listeners;

import ar.edu.itba.arquimicro.ampq.payloads.LlmPayload;
import ar.edu.itba.arquimicro.ampq.util.QueueNames;
import ar.edu.itba.arquimicro.ampq.util.UriPaths;
import ar.edu.itba.arquimicro.rest.models.MessageHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Optional;

@Component
public class InputListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputListener.class);
    private final RestClient restClient;
    private final RabbitTemplate rabbitTemplate;

    public InputListener(RestClient restClient, RabbitTemplate rabbitTemplate) {
        this.restClient = restClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Receive input from api gw
    @RabbitListener(queues = QueueNames.PROCESS_INPUT)
    public void receiveInput(String input, String chatId) {
        // Recibir input
        LOGGER.info("Received [{}] for chat [{}] from [{}] queue", input, chatId, QueueNames.PROCESS_INPUT);

        // Pedir conversaciones asociadas al input
        LOGGER.info("Requesting conversations associated with input [{}]", input);
        final Optional<MessageHistory> messageHistory = Optional.ofNullable(
                restClient.get()
                        .uri(String.join(UriPaths.DELIMITER, UriPaths.MESSAGES, "{chatId}"), chatId)
                        .retrieve()
                        .body(MessageHistory.class)
        );

        // Enviar input y history a la cola de LLM Manager
        final LlmPayload llmPayload = new LlmPayload(
                Integer.parseInt(chatId),
                input,
                messageHistory.map(MessageHistory::messages).orElse(Collections.emptyList())
        );

        LOGGER.info("Sending input [{}] to [{}] queue", input, QueueNames.SEND_LLM);
        rabbitTemplate.convertAndSend(QueueNames.SEND_LLM, llmPayload);
    }
}
