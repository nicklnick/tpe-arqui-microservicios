package g1.arquimicroservicios.apigw.services.contracts;

import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;

import java.util.Optional;

public interface IUsersService {

    Optional<Boolean> register(String email, String password, String name, String role);


    // returns role on call, null otherwise
    UserSignInResponseDto signIn(String email, String password);

}
