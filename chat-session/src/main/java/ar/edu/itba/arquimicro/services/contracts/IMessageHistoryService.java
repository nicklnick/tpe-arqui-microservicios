package ar.edu.itba.arquimicro.services.contracts;

import ar.edu.itba.arquimicro.services.models.MessageHistory;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IMessageHistoryService {
    MessageHistory getMessageHistory(int chatId, int limit);

    void saveMessageToHistory(MessageToHistory message) throws JsonProcessingException;
}
