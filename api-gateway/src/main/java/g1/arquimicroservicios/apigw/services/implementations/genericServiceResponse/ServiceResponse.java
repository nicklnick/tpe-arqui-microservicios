package g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse;

public record ServiceResponse<T,E>(T value, E httpStatusResponse) { }
