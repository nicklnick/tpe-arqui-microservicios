using ChatsApi.Controllers.Dtos;
using ChatsApi.Models;
using ChatsApi.ServicesInterface;
using Microsoft.AspNetCore.Mvc;

namespace ChatsApi.Controllers;


[ApiController]
[Route("api/chats")]
public class ChatsController(IChatsService chatsService) : ControllerBase
{


    [HttpGet("{userId:int}")]
    public async Task<IActionResult> GetUserChats([FromRoute] int userId)
    {
        var userChats = await chatsService.GetUser(userId);
        if (userChats.Count == 0)
        {
            return NoContent();
        }

        var dtoList = userChats.Select(chat => new ChatDto(chat.ChatId, chat.ChatName)).ToList();
        return Ok(dtoList);

    }

    [HttpPost("{userId:int}")]
    public async Task<IActionResult> AddUserChat([FromRoute] int userId, [FromBody] CreateChatDto chatNamePayload)
    {
        var chat = await chatsService.AddChat(userId, chatNamePayload.ChatName);
        return chat == null ? Conflict( "Chat with given name already exists") : CreatedAtAction(nameof(GetUserChats), new { userId = userId }, chat);;
    }
    
}