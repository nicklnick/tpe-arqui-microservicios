using Api.EntityFrameworkConfig;
using Api.Models;
using Api.ServicesInterfaces;
using Microsoft.EntityFrameworkCore;

namespace Api.ServicesImplementation;

public class MessageHistoryService(MessagesDbContext context) : IMessageHistoryService
{
    public async Task<Message[]> FindMessagesForChat(int chatId, int offset, int limit)
    {
        var messages = await context.Messages.Where(message => message.ChatId == chatId).OrderByDescending(message => message.Id)
        .Skip(offset).Take(limit).ToArrayAsync();
        return messages;
    }

    public async Task<bool> UploadMessage(string question, string answer, int chatId)
    {
        var newMessage = new Message { Question = question, ChatId = chatId, Answer = answer };

        try
        {
            await context.Messages.AddAsync(newMessage);
            await context.SaveChangesAsync();
            return true;
        }
        catch (DbUpdateException e)
        {
            return false;
        }

    }
}