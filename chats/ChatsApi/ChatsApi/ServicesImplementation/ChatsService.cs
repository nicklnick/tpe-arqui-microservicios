using ChatsApi.Models;
using ChatsApi.ServicesInterface;
using Microsoft.EntityFrameworkCore;

namespace ChatsApi.ServicesImplementation;

public class ChatsService(ChatDbContext context) : IChatsService
{
    public async Task<Chat?> AddChat(int userId, string chatName)
    {
        var userHasExistingChatWithSameName = context.Chats.Any(chat => chat.ChatName == chatName && chat.UserId == userId);
        if (userHasExistingChatWithSameName)
        {
            return null;
        }
        var newChat = new Chat() { UserId = userId, ChatName = chatName };
        context.Chats.Add(newChat);
        await context.SaveChangesAsync();
        return newChat;
    }

    public async Task<List<Chat>> GetUser(int userId)
    {
        var user = await context.Chats
            .Where(chat => chat.UserId == userId)
            .OrderByDescending(chat => chat.ChatId)
            .ToListAsync();

        return user;
    }
    
    
}