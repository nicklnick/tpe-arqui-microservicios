package ar.edu.itba.arquimicro.services.contracts;

import ar.edu.itba.arquimicro.services.models.LlmPayload;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ILlmService {

    void sendMessageToLlm(LlmPayload payload) throws JsonProcessingException;

}
