package ar.edu.itba.arquimicro.ampqcontrollers.amqpcontrollers;

import ar.edu.itba.arquimicro.ampqcontrollers.listeners.InputListener;
import ar.edu.itba.arquimicro.ampqcontrollers.payloads.InputRequestPayload;
import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.ILlmService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import ar.edu.itba.arquimicro.services.models.LlmPayload;
import ar.edu.itba.arquimicro.services.models.Message;
import ar.edu.itba.arquimicro.services.models.MessageHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputListenerTests {

    @Mock
    private IMessageHistoryService messageHistoryService;

    @Mock
    private ICacheService cacheService;

    @Mock
    private ILlmService llmService;

    @InjectMocks
    private InputListener inputListener;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveInput_Success() throws JsonProcessingException {
        String sessionId = "session123";
        int chatId = 42;
        String question = "What is the answer to life, the universe, and everything?";
        List<Message> previousMessages = List.of(new Message(1, "Question 1", "Answer 1"), new Message(2, "Question 2", "Answer 2"));

        InputRequestPayload inputPayload = new InputRequestPayload(sessionId, chatId, question);
        String inputJson = mapper.writeValueAsString(inputPayload);

        MessageHistory messageHistory = new MessageHistory(chatId, previousMessages);

        when(messageHistoryService.getMessageHistory(anyInt(), anyInt())).thenReturn(messageHistory);
        doNothing().when(cacheService).put(anyString(), any(CacheableMessageData.class));
        doNothing().when(llmService).sendMessageToLlm(any(LlmPayload.class));

        inputListener.receiveInput(inputJson);

        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CacheableMessageData> cacheableMessageDataCaptor = ArgumentCaptor.forClass(CacheableMessageData.class);
        ArgumentCaptor<LlmPayload> llmPayloadCaptor = ArgumentCaptor.forClass(LlmPayload.class);

        verify(messageHistoryService, times(1)).getMessageHistory(eq(chatId), eq(5));
        verify(cacheService, times(1)).put(messageIdCaptor.capture(), cacheableMessageDataCaptor.capture());
        verify(llmService, times(1)).sendMessageToLlm(llmPayloadCaptor.capture());

        CacheableMessageData cacheableMessageData = cacheableMessageDataCaptor.getValue();
        assertEquals(sessionId, cacheableMessageData.sessionId());
        assertEquals(chatId, cacheableMessageData.chatId());

        LlmPayload llmPayload = llmPayloadCaptor.getValue();
        assertEquals(question, llmPayload.question());
        assertEquals(previousMessages, llmPayload.messages());
    }
}
