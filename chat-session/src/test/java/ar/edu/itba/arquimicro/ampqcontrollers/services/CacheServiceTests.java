package ar.edu.itba.arquimicro.ampqcontrollers.services;

import ar.edu.itba.arquimicro.services.implementations.CacheService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CacheServiceTests {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testPut_Success() throws JsonProcessingException {
        String messageId = "message123";
        CacheableMessageData messageData = new CacheableMessageData("session123", 1);
        String cacheableMessageData = mapper.writeValueAsString(messageData);

        doNothing().when(valueOperations).set(anyString(), anyString(), any(Duration.class));
        cacheService.put(messageId, messageData);
        verify(valueOperations, times(1)).set(messageId, cacheableMessageData, Duration.ofMinutes(5));
    }

    @Test
    void testFind_Success() throws JsonProcessingException {
        String messageId = "message123";
        CacheableMessageData expectedMessageData = new CacheableMessageData("session123", 1);
        String cacheableMessageData = mapper.writeValueAsString(expectedMessageData);

        when(valueOperations.get(anyString())).thenReturn(cacheableMessageData);

        CacheableMessageData actualMessageData = cacheService.find(messageId);

        assertNotNull(actualMessageData);
        assertEquals(expectedMessageData, actualMessageData);
        verify(valueOperations, times(1)).get(messageId);
    }

    @Test
    void testFind_NotFound() throws JsonProcessingException {
        String messageId = "123";

        when(valueOperations.get(anyString())).thenReturn(null);

        assertThrows(IllegalArgumentException.class,() -> cacheService.find(messageId));

        verify(valueOperations, times(1)).get(messageId);
    }

    @Test
    void testFind_InvalidJson() throws JsonProcessingException {
        String messageId = "message123";
        String invalidJson = "invalidJson";

        when(valueOperations.get(anyString())).thenReturn(invalidJson);

        assertThrows(JsonProcessingException.class, () -> cacheService.find(messageId));

        verify(valueOperations, times(1)).get(messageId);
    }
}
