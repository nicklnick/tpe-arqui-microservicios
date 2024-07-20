namespace Api.Models;


public class Message
{
    public int ChatId { get; set; }
    public string Question { get; set; } = string.Empty;
    public string Answer { get; set; } = string.Empty;
    public int Id { get; set; }
}
