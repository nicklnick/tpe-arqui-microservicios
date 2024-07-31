package g1.arquimicroservicios.apigw.controllers.requestsDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiUserSignInRequest {
    private String email;
    private String password;
}
