package g1.arquimicroservicios.apigw.services.implementations.responseDtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageHistoryServiceResponseDto {
    private String question;
    private String answer;
    private String id;
}
