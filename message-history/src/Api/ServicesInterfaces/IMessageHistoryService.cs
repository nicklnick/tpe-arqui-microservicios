using Api.Models;

namespace Api.ServicesInterfaces;

public interface IMessageHistoryService
{
    public Task<Message[]> FindMessagesForChat(int chatId);
    public Task<bool> UploadMessage(string question, string answer,int chatId);
}