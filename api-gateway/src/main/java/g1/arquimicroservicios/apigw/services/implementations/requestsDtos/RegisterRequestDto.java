package g1.arquimicroservicios.apigw.services.implementations.requestsDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class RegisterRequestDto {

    private String email;
    private String password;
    private String role;
    private String name;

}
