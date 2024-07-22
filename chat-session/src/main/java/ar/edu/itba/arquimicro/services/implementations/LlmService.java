package ar.edu.itba.arquimicro.services.implementations;


import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.ILlmService;
import ar.edu.itba.arquimicro.services.models.LlmPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class LlmService implements ILlmService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendMessageToLlm(LlmPayload payload) throws JsonProcessingException {
        String jsonData = mapper.writeValueAsString(payload);
        rabbitTemplate.convertAndSend(QUEUES_DATA.DIRECT_EXCHANGE_NAME,QUEUES_DATA.SEND_LLM_DATA.SEND_LLM_ROUTING_KEY,jsonData);
    }
}
