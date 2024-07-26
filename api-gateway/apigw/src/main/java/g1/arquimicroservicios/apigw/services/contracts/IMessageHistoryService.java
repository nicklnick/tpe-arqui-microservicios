package g1.arquimicroservicios.apigw.services.contracts;

import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;

import java.util.List;

public interface IMessageHistoryService {

    List<MessageHistoryServiceResponseDto> getMessageHistory(int chatId, int page);
}
