using System.Text.Json.Serialization;

namespace ChatsApi.Controllers.Dtos;

public record ChatDto(
    [property: JsonPropertyName("chatId")] int ChatId,
    [property: JsonPropertyName("chatName")] string ChatName);