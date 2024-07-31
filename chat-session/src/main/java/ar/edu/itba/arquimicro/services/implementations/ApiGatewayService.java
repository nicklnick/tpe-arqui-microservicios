package ar.edu.itba.arquimicro.services.implementations;

import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.IApiGatewayService;
import ar.edu.itba.arquimicro.services.models.InputResponsePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ApiGatewayService implements IApiGatewayService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void sendQuestionResponse(InputResponsePayload payload) throws JsonProcessingException {
        String jsonData = mapper.writeValueAsString(payload);
        rabbitTemplate.convertAndSend(QUEUES_DATA.FANOUT_EXCHANGE_RESPONSE_LLM, "",jsonData);
    }
}
