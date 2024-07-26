using ChatsApi.Controllers;
using ChatsApi.Models;
using ChatsApi.ServicesImplementation;
using EntityFrameworkCoreMock;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace TestProject1;

public class ChatsIntegrationTest
{
    private DbContextMock<ChatDbContext> _contextMock;

    public ChatsIntegrationTest()
    {
        var options = new DbContextOptionsBuilder<ChatDbContext>().Options;
        _contextMock = new DbContextMock<ChatDbContext>(options);
    }


    private ChatsController GetChatsController() => new(new ChatsService(_contextMock.Object));


    [Fact]
    public async Task GetUserChats_Should_ReturnNoContent_When_NoChatsPresent()
    {
        List<Chat> initialData = [];

        _contextMock.CreateDbSetMock(m => m.Chats, initialData);
        

        var result = await GetChatsController().GetUserChats(1);

        Assert.IsType<NoContentResult>(result);
    }

    [Fact]
    public async Task GetUserChats_Should_ReturnOk_WhenContentFound()
    {
        var chatName = "chat1";
        var chatId = 1;
        var userId = 2;
        List<Chat> initialData = [new Chat { ChatId = chatId, ChatName = chatName, UserId = userId}];

        _contextMock.CreateDbSetMock(m => m.Chats, initialData);
        

        var result = await GetChatsController().GetUserChats(userId);

        Assert.IsType<OkObjectResult>(result);
    }

    [Fact]
    public async Task AddUserChat_Should_ReturnConflict_WhenChatWithGivenNameAlreadyExists()
    {
        var chatName = "chat1";
        var chatId = 1;
        var userId = 2;
        List<Chat> initialData = [new Chat { ChatId = chatId, ChatName = chatName, UserId = userId}];

        _contextMock.CreateDbSetMock(m => m.Chats, initialData);
        
        var result = await GetChatsController().AddUserChat(userId,chatName);

        Assert.IsType<ConflictObjectResult>(result);
        
    }

    [Fact]
    public async Task AddUserChat_Should_ReturnCreated_WhenChatDoesntExists()
    {
        var chatName = "chat1";
        var chatId = 1;
        var userId = 2;
        List<Chat> initialData = [new Chat { ChatId = chatId, ChatName = chatName, UserId = userId}];

        _contextMock.CreateDbSetMock(m => m.Chats, initialData);
        
        var result = await GetChatsController().AddUserChat(userId,"chat2");

        Assert.IsType<CreatedResult>(result);
        
    }
}