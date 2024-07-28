package g1.arquimicroservicios.apigw.controllers.responseDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageHistoryResponseDto {
    private String question;
    private String answer;
    private int id;
}
