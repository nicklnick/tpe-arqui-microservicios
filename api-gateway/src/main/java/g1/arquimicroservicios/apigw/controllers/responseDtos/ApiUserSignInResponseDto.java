package g1.arquimicroservicios.apigw.controllers.responseDtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiUserSignInResponseDto {
    private String email;
    private String role;
    private int userId;
    private String name;
}
