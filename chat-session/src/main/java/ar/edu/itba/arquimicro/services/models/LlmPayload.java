package ar.edu.itba.arquimicro.services.models;

import java.util.List;

public record LlmPayload(int chatId, String messageId, String question, List<Message> messages) {
}
