namespace ChatsApi.Models;

public class Chat
{
    public int ChatId { get; set; }
    public string ChatName { get; set; } = string.Empty;
    public int UserId { get; set; }
}

