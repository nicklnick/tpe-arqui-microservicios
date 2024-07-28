package g1.arquimicroservicios.apigw.services.implementations.responseDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageHistoryServiceResponseDto {
    private String question;
    private String answer;
    private int id;
}
