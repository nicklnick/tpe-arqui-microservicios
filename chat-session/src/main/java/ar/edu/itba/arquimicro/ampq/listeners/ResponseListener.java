package ar.edu.itba.arquimicro.ampq.listeners;

import ar.edu.itba.arquimicro.ampq.payloads.InputResponsePayload;
import ar.edu.itba.arquimicro.ampq.util.QueueNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
//
//@Component
//public class ResponseListener {
//    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseListener.class);
//    private final RabbitTemplate rabbitTemplate;
//
//    public ResponseListener(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    // Receive response from LLM Manager
//    @RabbitListener(queues = QueueNames.RECEIVE_LLM)
//    public void receiveResponse(String input, String response, String chatId) {
//        // Recibir respuesta
//        LOGGER.info("Received response [{}] for input [{}] from [{}] queue", response, input, QueueNames.RECEIVE_LLM);
//
//        // Enviar a que se guarde al historial de mensajes
//        LOGGER.info("Sending response [{}] to [{}] queue", response, QueueNames.SAVE_RESPONSE);
//        final InputResponsePayload payload = new InputResponsePayload(Integer.parseInt(chatId), input, response);
//        rabbitTemplate.convertAndSend(QueueNames.SAVE_RESPONSE, payload);
//
//        // Enviar al api gw
//        LOGGER.info("Sending response [{}] to [{}] queue", response, QueueNames.SEND_RESPONSE_TO_USER);
//        rabbitTemplate.convertAndSend(QueueNames.SEND_RESPONSE_TO_USER, payload);
//    }
//}
