package ar.edu.itba.arquimicro.services.contracts;

import ar.edu.itba.arquimicro.services.models.CacheableMessageData;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ICacheService {

    void put(String messageId, CacheableMessageData messageData) throws JsonProcessingException;
    CacheableMessageData find(String messageId) throws JsonProcessingException;

}
