package ar.edu.itba.arquimicro.ampqcontrollers.payloads;

public record InputRequestPayload(String sessionId, int chatId, String question) {
}
