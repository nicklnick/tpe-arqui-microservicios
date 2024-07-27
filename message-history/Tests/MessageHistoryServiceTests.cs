using Api.EntityFrameworkConfig;
using Api.ServicesImplementation;
using Api.ServicesInterfaces;
using EntityFrameworkCoreMock;
using Microsoft.EntityFrameworkCore;
using Moq;
using Message = Api.Models.Message;

namespace Tests;

public class MessageHistoryServiceTests
{
    private readonly DbContextMock<MessagesDbContext> _dbContextMock;
    private readonly IMessageHistoryService _service;
    
    public MessageHistoryServiceTests()
    {
        var options = new DbContextOptionsBuilder<MessagesDbContext>().Options;
        _dbContextMock = new DbContextMock<MessagesDbContext>(options);
        _service = new MessageHistoryService(_dbContextMock.Object);
    }

    

    [Fact]
    public async Task FindMessagesForChat_Should_ReturnEmptyArray_When_NoChatsFound()
    {
        //Arrange
        List<Message> initialData = [];
        _dbContextMock.CreateDbSetMock(x => x.Messages, initialData);
        //Act
        var chats = await _service.FindMessagesForChat(1, 0,10);
        Assert.Empty(chats);
    }
    
 

    [Fact]
    public async Task FindMessagesForChat_Should_LimitIncomingMessages()
    {
        //Arrange
        List<Message> initialData = [new Message {Answer = "Answer1",ChatId = 1,Id = 1,Question = "Question1"},
            new Message {Answer = "Answer2",ChatId = 1,Id = 2,Question = "Question2"},
            new Message {Answer = "Answer3",ChatId = 1,Id = 3,Question = "Question3"},
            new Message {Answer = "Answer4",ChatId = 1,Id = 4,Question = "Question4"}];
        _dbContextMock.CreateDbSetMock(x => x.Messages, initialData);
        
        //Act
        var chats = await _service.FindMessagesForChat(1, 0,3);
        Assert.Equal(3,chats.Length);
        Assert.True(chats.ToList().Exists( q => q.Id == 4));
    }

    [Fact]
    public async Task FindMessagesForChat_Should_SkipMessages()
    {
        //Arrange
        List<Message> initialData = [new Message {Answer = "Answer1",ChatId = 1,Id = 1,Question = "Question1"},
            new Message {Answer = "Answer2",ChatId = 1,Id = 2,Question = "Question2"},
            new Message {Answer = "Answer3",ChatId = 1,Id = 3,Question = "Question3"},
            new Message {Answer = "Answer4",ChatId = 1,Id = 4,Question = "Question4"}];
        _dbContextMock.CreateDbSetMock(x => x.Messages, initialData);
        
        //Act
        var chats = await _service.FindMessagesForChat(1, 3,1);
        Assert.Single(chats);
        Assert.True(chats.ToList().Exists( q => q.Id == 1));
    }

    [Fact]
    public async Task FindMessagesForChat_Should_RetrieveMessagesCorrectly()
    {
        //Arrange
        List<Message> initialData = [new Message {Answer = "Answer1",ChatId = 1,Id = 1,Question = "Question1"},
            new Message {Answer = "Answer2",ChatId = 2,Id = 2,Question = "Question2"},
            new Message {Answer = "Answer2a",ChatId = 2,Id = 5,Question = "Question2a"},

            new Message {Answer = "Answer3",ChatId = 3,Id = 3,Question = "Question3"},
            new Message {Answer = "Answer4",ChatId = 4,Id = 4,Question = "Question4"}];
        _dbContextMock.CreateDbSetMock(x => x.Messages, initialData);
        
        //Act
        var chats = await _service.FindMessagesForChat(2, 0,5);
        Assert.Equal(2, chats.Length);
        Assert.True(chats.ToList().Exists(msg => msg.Id == 2));
        Assert.True(chats.ToList().TrueForAll(msg => msg.ChatId == 2));
        
    }
    
    
}