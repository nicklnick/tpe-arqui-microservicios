package g1.arquimicroservicios.apigw.controllers.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.constants.RabbitMQConstants;
import g1.arquimicroservicios.apigw.controllers.websocket.dtos.QuestionMessage;
import g1.arquimicroservicios.apigw.controllers.websocket.dtos.WebSocketMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        } else if (webSocketMessage instanceof QuestionMessage questionMessage) {
            rabbitTemplate.convertAndSend(RabbitMQConstants.QUESTION_EXCHANGE, RabbitMQConstants.QUESTION_ROUTING_KEY, questionMessage.getContent());
            session.sendMessage(new TextMessage("Hello man guy, " + questionMessage.getContent()));
        }
    }



}