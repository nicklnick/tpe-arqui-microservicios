package g1.arquimicroservicios.apigw.controllers.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatsResponseDto {
    private int chatId;
    private String chatName;
}
