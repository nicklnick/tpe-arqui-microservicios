package ar.edu.itba.arquimicro.ampq.payloads;

public record InputRequestPayload(String sessionId, int chatId, String question) {
}
