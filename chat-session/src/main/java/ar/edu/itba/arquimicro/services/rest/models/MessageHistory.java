package ar.edu.itba.arquimicro.services.rest.models;

import java.util.List;

public record MessageHistory(int chatId, List<Message> messages) {
}
