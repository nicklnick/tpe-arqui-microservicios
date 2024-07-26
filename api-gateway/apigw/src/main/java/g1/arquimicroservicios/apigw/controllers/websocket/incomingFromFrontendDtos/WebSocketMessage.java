package g1.arquimicroservicios.apigw.controllers.websocket.incomingFromFrontendDtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuestionMessage.class, name = "question"),
})
public abstract class WebSocketMessage {
    private  @Getter @Setter String  type;
}