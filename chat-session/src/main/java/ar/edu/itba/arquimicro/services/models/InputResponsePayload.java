package ar.edu.itba.arquimicro.services.models;

public record InputResponsePayload(String sessionId,int chatId, String question, String answer) {
}
