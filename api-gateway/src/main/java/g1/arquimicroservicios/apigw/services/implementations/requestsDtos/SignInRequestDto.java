package g1.arquimicroservicios.apigw.services.implementations.requestsDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignInRequestDto {
    private String email;
    private String password;
}
