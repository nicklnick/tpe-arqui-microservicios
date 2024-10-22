package g1.arquimicroservicios.apigw.controllers;


import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserCreateChatRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserRegisterRequest;
import g1.arquimicroservicios.apigw.controllers.requestsDtos.ApiUserSignInRequest;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ApiUserSignInResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.ChatsResponseDto;
import g1.arquimicroservicios.apigw.controllers.responseDtos.MessageHistoryResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IChatsService;
import g1.arquimicroservicios.apigw.services.contracts.IMessageHistoryService;
import g1.arquimicroservicios.apigw.services.contracts.IUsersService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.ChatsServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.MessageHistoryServiceResponseDto;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.UserSignInResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final IUsersService service;


    //Register
    @PostMapping()
    public ResponseEntity<?> response(@RequestBody ApiUserRegisterRequest userRegisterRequest) {
        Optional<Boolean> maybeRegisterSuccess = service.register(userRegisterRequest.getEmail(), userRegisterRequest.getPassword(), userRegisterRequest.getName(), userRegisterRequest.getRole());
        if (maybeRegisterSuccess.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        boolean registerSuccess = maybeRegisterSuccess.get();
        if (!registerSuccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //SignIn
    @PostMapping("signIn")
    public ResponseEntity<ApiUserSignInResponseDto> signInResponse(@RequestBody ApiUserSignInRequest userSignInRequest) {
        UserSignInResponseDto maybeUser = service.signIn(userSignInRequest.getEmail(), userSignInRequest.getPassword());
        if (maybeUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new ApiUserSignInResponseDto(userSignInRequest.getEmail(), maybeUser.role(), maybeUser.userId(), maybeUser.name()));
    }

    // Chats --------------------------------------------------------------------------------------------------------------------
    private final IChatsService chatsService;

    @GetMapping("{userId}/chats")
    public ResponseEntity<List<ChatsResponseDto>> getChatTitles(@PathVariable("userId") int userId) {
        List<ChatsServiceResponseDto> chatsList = chatsService.getUserChats(userId);
        if (chatsList == null) {
            return ResponseEntity.internalServerError().build();
        }

        if (chatsList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chatsList.stream().map(chat -> new ChatsResponseDto(chat.getChatId(), chat.getChatName())).toList());
    }

    @PostMapping("{userId}/chats")
    public ResponseEntity<?> createChat(@PathVariable("userId") int userId,@RequestBody ApiUserCreateChatRequest payload) {
        ServiceResponse<Integer,HttpStatus> maybeCreated = chatsService.createUserChat(userId, payload.chatName());
        if (maybeCreated.httpStatusResponse().is2xxSuccessful()){
            return ResponseEntity.status(HttpStatus.CREATED).headers(headers -> headers.set("Location","/api/users/" + userId + "/chats/" +maybeCreated.value()))
                    .body(Map.of("id",maybeCreated.value()));
        }
        return  ResponseEntity.status(maybeCreated.httpStatusResponse()).build();
    }


    private IMessageHistoryService messageHistoryService;

    @GetMapping("{userId}/chats/{chatId}")
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
