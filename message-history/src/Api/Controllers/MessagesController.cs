using Api.ServicesInterfaces;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers;

[ApiController]
[Route("api/messages")]
public class MessagesController(IMessageHistoryService messageHistoryService) : ControllerBase
{
    [HttpGet("{chatId:int}")]
    public async Task<IActionResult> GetMessageHistory([FromRoute] int chatId, [FromQuery] int offset = 0, [FromQuery] int limit = 10)
    {
        var messages = await messageHistoryService.FindMessagesForChat(chatId,offset,limit);
        if (messages.Length == 0)
        {
            return NoContent();
        }
        var toReturn  = messages.Select(message => new { message.Question, message.Answer, message.Id}).ToArray();
        return Ok(toReturn);
    }
}
