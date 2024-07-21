package g1.arquimicroservicios.apigw.controllers.websocket.forwardingToBackendDtos;

public record QuestionDto(String sessionId, int chatId,String question) {
}
