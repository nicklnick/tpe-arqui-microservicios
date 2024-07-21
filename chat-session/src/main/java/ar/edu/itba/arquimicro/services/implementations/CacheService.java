package ar.edu.itba.arquimicro.services.implementations;

import ar.edu.itba.arquimicro.services.contracts.ICacheService;
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

    public void put(String messageId, String sessionId) {
        redisTemplate.opsForValue().set(messageId, sessionId, Duration.ofMinutes(5));
    }

    public String find(String messageId) {
        return redisTemplate.opsForValue().get(messageId);
    }


}
