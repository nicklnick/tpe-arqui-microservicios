using Api.EntityFrameworkConfig;
using Api.ServicesImplementation;
using Api.ServicesInterfaces;
using EntityFrameworkCoreMock;
using Microsoft.EntityFrameworkCore;
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
        
        
        
        
    }
}