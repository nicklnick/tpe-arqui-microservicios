using System.Text.Json.Serialization;

namespace Api.MessageQueueUtils;

public sealed record QueueMessage(
    [property: JsonPropertyName("question")] string Question,
    [property: JsonPropertyName("answer")] string Answer,
    [property: JsonPropertyName("chatId")] int ChatId
    ){};