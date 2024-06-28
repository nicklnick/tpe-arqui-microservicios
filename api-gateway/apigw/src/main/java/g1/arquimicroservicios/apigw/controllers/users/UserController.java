package g1.arquimicroservicios.apigw.controllers.users;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    @GetMapping("hello")
    public String response(){
        return "Hello";
    }
}
