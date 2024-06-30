package g1.arquimicroservicios.apigw.controllers.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import g1.arquimicroservicios.apigw.controllers.websocket.dtos.FarewellMessage;
import g1.arquimicroservicios.apigw.controllers.websocket.dtos.GreetingMessage;
import g1.arquimicroservicios.apigw.controllers.websocket.dtos.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class WebSocketImplementation extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);
        WebSocketMessage webSocketMessage = objectMapper.readValue(payload, WebSocketMessage.class);
        if (webSocketMessage == null){
            session.sendMessage(new TextMessage("Error"));
        } else if (webSocketMessage instanceof GreetingMessage greetingMessage) {
            session.sendMessage(new TextMessage("Hello man guy, " + greetingMessage.getContent()));
        } else if (webSocketMessage instanceof FarewellMessage farewellMessage) {
            session.sendMessage(new TextMessage("Goodbye, " + farewellMessage.getContent()));
        }
    }
}