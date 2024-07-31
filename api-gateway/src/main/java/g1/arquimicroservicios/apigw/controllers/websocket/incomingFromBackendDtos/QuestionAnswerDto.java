package g1.arquimicroservicios.apigw.controllers.websocket.incomingFromBackendDtos;

public record QuestionAnswerDto(String sessionId,int chatId, String question, String answer) {
}
