package ar.edu.itba.arquimicro.ampq.payloads;

import ar.edu.itba.arquimicro.rest.models.Message;

import java.util.List;

public record LlmPayload(int chatId, String input, List<Message> messages) {
}
