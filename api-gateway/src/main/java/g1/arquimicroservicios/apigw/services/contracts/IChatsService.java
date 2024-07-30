package g1.arquimicroservicios.apigw.services.contracts;

import java.util.List;
import java.util.Optional;

import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import org.springframework.http.HttpStatus;

public interface IChatsService {    
    List<ChatsServiceResponseDto> getUserChats(int userId);

    ServiceResponse<Integer, HttpStatus> createUserChat(int userId, String chatName);
}
