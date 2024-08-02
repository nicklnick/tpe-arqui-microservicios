package ar.edu.itba.arquimicro.ampqcontrollers.amqpcontrollers;

import ar.edu.itba.arquimicro.ampqcontrollers.listeners.ResponseListener;
import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import ar.edu.itba.arquimicro.services.contracts.IApiGatewayService;
import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.contracts.IMessageHistoryService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import ar.edu.itba.arquimicro.services.models.InputResponsePayload;
import ar.edu.itba.arquimicro.services.models.LlmResponse;
import ar.edu.itba.arquimicro.services.models.MessageToHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ResponseListenerTests {

    @Mock
    private ICacheService cacheService;

    @Mock
    private IMessageHistoryService messageHistoryService;

    @Mock
    private IApiGatewayService apiGatewayService;

    @InjectMocks
    private ResponseListener responseListener;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveResponse_Success() throws JsonProcessingException {
        String messageId = "message123";
        String sessionId = "session123";
        int chatId = 42;
        String question = "What is the answer to life, the universe, and everything?";
        String answer = "42";
        LlmResponse response = new LlmResponse(messageId, question, answer);
        String jsonResponse = mapper.writeValueAsString(response);
        CacheableMessageData messageData = new CacheableMessageData(sessionId, chatId);
        when(cacheService.find(anyString())).thenReturn(messageData);
        doNothing().when(messageHistoryService).saveMessageToHistory(any(MessageToHistory.class));
        doNothing().when(apiGatewayService).sendQuestionResponse(any(InputResponsePayload.class));
        responseListener.receiveResponse(jsonResponse);
        ArgumentCaptor<MessageToHistory> messageToHistoryCaptor = ArgumentCaptor.forClass(MessageToHistory.class);
        ArgumentCaptor<InputResponsePayload> inputResponsePayloadCaptor = ArgumentCaptor.forClass(InputResponsePayload.class);

        verify(cacheService, times(1)).find(eq(messageId));
        verify(messageHistoryService, times(1)).saveMessageToHistory(messageToHistoryCaptor.capture());
        verify(apiGatewayService, times(1)).sendQuestionResponse(inputResponsePayloadCaptor.capture());

        MessageToHistory messageToHistory = messageToHistoryCaptor.getValue();
        assertEquals(chatId, messageToHistory.chatId());
        assertEquals(question, messageToHistory.question());
        assertEquals(answer, messageToHistory.answer());

        InputResponsePayload inputResponsePayload = inputResponsePayloadCaptor.getValue();
        assertEquals(sessionId, inputResponsePayload.sessionId());
        assertEquals(chatId, inputResponsePayload.chatId());
        assertEquals(question, inputResponsePayload.question());
        assertEquals(answer, inputResponsePayload.answer());
    }
}
