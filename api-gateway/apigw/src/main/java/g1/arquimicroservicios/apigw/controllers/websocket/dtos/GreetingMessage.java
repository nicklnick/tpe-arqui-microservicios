package g1.arquimicroservicios.apigw.controllers.websocket.dtos;

import lombok.Getter;
import lombok.Setter;


public class GreetingMessage extends WebSocketMessage {
    private @Getter @Setter String content;

    public GreetingMessage() {
        this.setType("greeting");
    }


}