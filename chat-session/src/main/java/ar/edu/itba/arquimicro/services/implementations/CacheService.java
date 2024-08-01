package ar.edu.itba.arquimicro.services.implementations;

import ar.edu.itba.arquimicro.services.contracts.ICacheService;
import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.*;



@Service
@AllArgsConstructor
public class CacheService implements ICacheService  {


    //maps messageId => sessionId
    private RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    public void put(String messageId, CacheableMessageData messageData) throws JsonProcessingException {
        String cacheableMessageData = mapper.writeValueAsString(messageData);
        redisTemplate.opsForValue().set(messageId, cacheableMessageData, Duration.ofMinutes(5));
    }

    public CacheableMessageData find(String messageId) throws JsonProcessingException {
        String data = redisTemplate.opsForValue().get(messageId);
        if (data == null){
            throw new IllegalArgumentException("Cache data missing or expired");
        }
        return mapper.readValue(data,CacheableMessageData.class);
    }


}
