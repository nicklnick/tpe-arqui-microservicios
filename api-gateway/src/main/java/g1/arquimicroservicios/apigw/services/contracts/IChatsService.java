package g1.arquimicroservicios.apigw.services.contracts;

import java.util.List;
import java.util.Optional;

import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;

public interface IChatsService {    
    List<ChatsServiceResponseDto> getUserChats(int userId);

    Optional<Boolean> createUserChat(int userId, String chatName);
}
