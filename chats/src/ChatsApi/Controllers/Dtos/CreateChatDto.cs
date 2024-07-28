using System.Text.Json.Serialization;



namespace ChatsApi.Controllers.Dtos;

public record CreateChatDto(
    [property: JsonPropertyName("chatName")] string ChatName);