package g1.arquimicroservicios.apigw.services.implementations.responseDtos;

public record UserSignInResponseDto(int userId, String name, String email, String role) {
}