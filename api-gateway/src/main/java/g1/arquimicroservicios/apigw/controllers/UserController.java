package g1.arquimicroservicios.apigw.controllers;


import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserRegisterRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserSignInRequest;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ApiUserSignInResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IUsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUsersService service;

    @PostMapping("register")
    public ResponseEntity<?> response(@RequestBody ApiUserRegisterRequest userRegisterRequest){
        boolean registerSuccess = service.register(userRegisterRequest.getEmail(), userRegisterRequest.getPassword(), userRegisterRequest.getName(), userRegisterRequest.getRole());
        if (!registerSuccess){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("signIn")
    public ResponseEntity<ApiUserSignInResponseDto> signInResponse(@RequestBody ApiUserSignInRequest userSignInRequest)
    {
        String maybeRole = service.signIn(userSignInRequest.getEmail(), userSignInRequest.getPassword());
        if (maybeRole == null)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new ApiUserSignInResponseDto(userSignInRequest.getEmail(), maybeRole));
    }

}
