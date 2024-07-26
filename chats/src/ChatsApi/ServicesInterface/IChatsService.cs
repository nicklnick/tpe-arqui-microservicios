using ChatsApi.Models;

namespace ChatsApi.ServicesInterface;

public interface IChatsService
{
    Task<Chat?> AddChat(int userId, string chatName);
    Task<List<Chat>> GetUser(int userId);
}