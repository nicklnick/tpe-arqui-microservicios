package g1.arquimicroservicios.apigw.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import g1.arquimicroservicios.apigw.controllers.responseDtos.ChatsResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/chats")
public class ChatsController {
    
    private final IChatsService chatsService;

    @GetMapping
    public ResponseEntity<List<ChatsResponseDto>> getChatTitles(@RequestHeader("userId") int userId){
        List<ChatsServiceResponseDto> chatsList = chatsService.getUserChats(userId);
        if (chatsList == null){
            return ResponseEntity.internalServerError().build();
        }

        if (chatsList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chatsList.stream().map(chat -> new ChatsResponseDto(chat.getChatId(),chat.getChatName())).toList());
    }

}
