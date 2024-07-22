package ar.edu.itba.arquimicro.services.models;

public record LlmResponse(int chatId, String messageId,String question, String answer) {
}
