package ar.edu.itba.arquimicro.services.contracts;

import ar.edu.itba.arquimicro.services.rest.models.MessageHistory;

public interface IMessageHistoryService {
    MessageHistory getMessageHistory(int chatId, int limit);
}
