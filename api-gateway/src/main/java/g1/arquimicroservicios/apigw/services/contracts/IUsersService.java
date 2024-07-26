package g1.arquimicroservicios.apigw.services.contracts;

public interface IUsersService {

    boolean register(String email, String password, String name,String role);


    // returns role on call, null otherwise
    String signIn(String email, String password);

}
