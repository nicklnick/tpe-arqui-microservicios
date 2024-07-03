using System.Text.Json.Serialization;

namespace Api.MessageQueueUtils;

public sealed record QueueMessage
{
    [JsonPropertyName("question")] public string Question { get; set; } = string.Empty;
    [JsonPropertyName("answer")] public string Answer { get; set; } = string.Empty;
    [JsonPropertyName("chatId")] public int ChatId { get; set; }
};