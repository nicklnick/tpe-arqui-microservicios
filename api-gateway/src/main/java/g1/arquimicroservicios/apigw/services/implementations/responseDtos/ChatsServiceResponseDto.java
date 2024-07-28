package g1.arquimicroservicios.apigw.services.implementations.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatsServiceResponseDto {
    private int chatId;
    private String chatName;
}
