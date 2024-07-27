package g1.arquimicroservicios.apigw.services.contracts;

import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;

public interface IUsersService {

    boolean register(String email, String password, String name,String role);


    // returns role on call, null otherwise
    UserSignInResponseDto signIn(String email, String password);

}
