package ar.edu.itba.arquimicro.rest.models;

import java.util.List;

public record MessageHistory(Chat chat, List<Message> messages) {
}
