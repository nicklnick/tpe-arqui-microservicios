package g1.arquimicroservicios.apigw.controllers.requestsDtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiUserSignInRequest {
    private String email;
    private String password;
}
