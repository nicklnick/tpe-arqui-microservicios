package g1.arquimicroservicios.apigw.controllers;


import g1.arquimicroservicios.apigw.controllers.responseDtos.MessageHistoryResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IMessageHistoryService;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/messageHistory")
public class MessageHistoryController {


    private IMessageHistoryService messageHistoryService;

    @GetMapping("{chatId}")
    public ResponseEntity<List<MessageHistoryResponseDto>> getMessageHistory(
            @PathVariable("chatId") int chatId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page)
    {

        List<MessageHistoryServiceResponseDto> messagesList = messageHistoryService.getMessageHistory(chatId, page);

        if (messagesList == null){
            return ResponseEntity.internalServerError().build();
        }

        if (messagesList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        List<MessageHistoryResponseDto> dtos = messagesList.stream().map(message -> new MessageHistoryResponseDto(message.getQuestion(), message.getAnswer(), message.getId())).toList();

        return ResponseEntity.ok(dtos);


    }
}
