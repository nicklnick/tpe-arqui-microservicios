package g1.arquimicroservicios.apigw.controllers.websocket.dtos;

import lombok.Getter;
import lombok.Setter;

public class FarewellMessage extends WebSocketMessage {
    private @Getter @Setter String content;

    public FarewellMessage() {
        this.setType("farewell");
    }
}