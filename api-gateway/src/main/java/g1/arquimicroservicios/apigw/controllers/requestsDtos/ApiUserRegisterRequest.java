package g1.arquimicroservicios.apigw.controllers.requestsDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApiUserRegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;
}
