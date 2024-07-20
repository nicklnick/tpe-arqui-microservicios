using Api.Models;

namespace Api.ServicesInterfaces;

public interface IMessageHistoryService
{
    public Task<Message[]> FindMessagesForChat(int chatId,int offset, int limit);
    public Task<bool> UploadMessage(string question, string answer,int chatId);
}