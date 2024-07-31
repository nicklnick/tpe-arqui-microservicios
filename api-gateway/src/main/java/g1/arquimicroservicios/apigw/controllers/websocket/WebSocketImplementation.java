package g1.arquimicroservicios.apigw.controllers.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.constants.RabbitMQConstants;
import g1.arquimicroservicios.apigw.controllers.websocket.forwardingToBackendDtos.QuestionDto;
import g1.arquimicroservicios.apigw.controllers.websocket.incomingFromBackendDtos.QuestionAnswerDto;
import g1.arquimicroservicios.apigw.controllers.websocket.incomingFromFrontendDtos.QuestionMessage;
import g1.arquimicroservicios.apigw.controllers.websocket.incomingFromFrontendDtos.WebSocketMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketImplementation extends TextWebSocketHandler {



    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();


    private final RabbitTemplate rabbitTemplate;

    public WebSocketImplementation(RabbitTemplate template) {
        rabbitTemplate = template;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMap.put(session.getId(),session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove session from the map when the connection is close
        sessionMap.remove(session.getId());
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);
        WebSocketMessage webSocketMessage = objectMapper.readValue(payload, WebSocketMessage.class);
        if (webSocketMessage == null){
            session.sendMessage(new TextMessage("Error"));
            return;
        }


        if (webSocketMessage instanceof QuestionMessage questionMessage) {
            QuestionDto dto = questionMessage.asQuestionDto(session.getId());
            String serializedMessage = objectMapper.writeValueAsString(dto);
            rabbitTemplate.convertAndSend(RabbitMQConstants.QUESTION_EXCHANGE, RabbitMQConstants.QUESTION_ROUTING_KEY, serializedMessage);
        }


    }

    @RabbitListener(queues = "#{llmConsumerQueue.name}")
    public void rabbitMQReceiver(String llmJsonResponse) throws IOException{
        QuestionAnswerDto response = objectMapper.readValue(llmJsonResponse, QuestionAnswerDto.class);
        if (sessionMap.containsKey(response.sessionId())){
            WebSocketSession wsConnection = sessionMap.get(response.sessionId());
            String answerToUserQuestion = objectMapper.writeValueAsString(response);
            wsConnection.sendMessage(new TextMessage(answerToUserQuestion));
            System.out.println("this message was destinied for me!");
        } else {
            System.out.println("this message was destinied for another worker");
        }
    }



}