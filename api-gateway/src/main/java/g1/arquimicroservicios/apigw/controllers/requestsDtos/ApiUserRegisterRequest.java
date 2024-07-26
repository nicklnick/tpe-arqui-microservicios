package g1.arquimicroservicios.apigw.controllers.requestsDtos;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiUserRegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;
}
