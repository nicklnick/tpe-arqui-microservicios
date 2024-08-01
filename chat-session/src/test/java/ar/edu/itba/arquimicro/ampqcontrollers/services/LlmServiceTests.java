package ar.edu.itba.arquimicro.ampqcontrollers.services;
import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.implementations.LlmService;
import ar.edu.itba.arquimicro.services.models.LlmPayload;
import ar.edu.itba.arquimicro.services.models.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LlmServiceTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private LlmService llmService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessageToLlm_Success() throws JsonProcessingException {
        List<Message> messages = List.of(new Message(1,"salute me", "Hello"), new Message(2,"say bye to me", "Goodbye"));
        LlmPayload payload = new LlmPayload("message123", "test question", messages);
        String jsonData = mapper.writeValueAsString(payload);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        llmService.sendMessageToLlm(payload);

        verify(rabbitTemplate, times(1)).convertAndSend(
                QUEUES_DATA.DIRECT_EXCHANGE_NAME,
                QUEUES_DATA.SEND_LLM_DATA.SEND_LLM_ROUTING_KEY,
                jsonData);
    }

}
