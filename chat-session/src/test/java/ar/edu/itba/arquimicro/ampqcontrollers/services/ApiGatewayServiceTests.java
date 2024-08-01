package ar.edu.itba.arquimicro.ampqcontrollers.services;


import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.implementations.ApiGatewayService;
import ar.edu.itba.arquimicro.services.models.InputResponsePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApiGatewayServiceTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ApiGatewayService apiGatewayService;


    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendQuestionResponse_Success() throws JsonProcessingException {
        InputResponsePayload payload = new InputResponsePayload("session123", 42, "What is the answer to life, the universe, and everything?", "42");
        String jsonData = mapper.writeValueAsString(payload);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        apiGatewayService.sendQuestionResponse(payload);

        verify(rabbitTemplate, times(1)).convertAndSend(QUEUES_DATA.FANOUT_EXCHANGE_RESPONSE_LLM, "", jsonData);
    }


}


