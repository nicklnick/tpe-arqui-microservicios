package ar.edu.itba.arquimicro.services.contracts;

import ar.edu.itba.arquimicro.services.models.InputResponsePayload;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IApiGatewayService {

    void sendQuestionResponse(InputResponsePayload payload) throws JsonProcessingException;
}
