package g1.arquimicroservicios.apigw.controllers.websocket.dtos;

import lombok.Getter;
import lombok.Setter;


public class QuestionMessage extends WebSocketMessage {
    private @Getter @Setter String content;

    public QuestionMessage() {
        this.setType("question");
    }

}