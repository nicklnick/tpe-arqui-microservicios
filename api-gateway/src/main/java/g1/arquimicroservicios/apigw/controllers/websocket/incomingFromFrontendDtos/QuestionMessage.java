package g1.arquimicroservicios.apigw.controllers.websocket.incomingFromFrontendDtos;

import g1.arquimicroservicios.apigw.controllers.websocket.forwardingToBackendDtos.QuestionDto;
import lombok.Getter;
import lombok.Setter;


public class QuestionMessage extends WebSocketMessage {
    private @Getter @Setter String question;
    private @Getter @Setter int chatId;

    public QuestionDto asQuestionDto(String sessionId){
        return new QuestionDto(sessionId,this.chatId, this.question);
    }
}
