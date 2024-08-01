package ar.edu.itba.arquimicro.ampqcontrollers.services;

import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.implementations.MessageHistoryService;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MessageHistoryServiceTests{


    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessageHistoryService messageHistoryService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMessageToHistory_Success() throws JsonProcessingException {
        MessageToHistory message = new MessageToHistory(1, "Question", "Answer");
        String jsonMessage = mapper.writeValueAsString(message);

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());

        messageHistoryService.saveMessageToHistory(message);

        verify(rabbitTemplate, times(1)).convertAndSend(
                QUEUES_DATA.DIRECT_EXCHANGE_NAME,
                QUEUES_DATA.SEND_MESSAGE_HISTORY_DATA.SEND_MESSAGE_HISTORY_ROUTING_KEY,
                jsonMessage);
    }

}
