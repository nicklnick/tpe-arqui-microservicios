using Api.Controllers;
using Api.EntityFrameworkConfig;
using Api.Models;
using Api.ServicesImplementation;
using EntityFrameworkCoreMock;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Tests;

public class MessageHistoryIntegrationTests
{
    private readonly DbContextMock<MessagesDbContext> _dbContext;
    private readonly MessagesController _controller;
    public MessageHistoryIntegrationTests()
    {
        var options = new DbContextOptionsBuilder<MessagesDbContext>().Options;
        _dbContext = new DbContextMock<MessagesDbContext>(options);
        var service = new MessageHistoryService(_dbContext.Object);
        _controller = new MessagesController(service);
    }

    [Fact]
    public async Task GetMessageHistory_Should_ReturnMessages_When_ThereAreMessages()
    {
        //Arrange
        List<Message> initialData = [new Message {Answer = "Answer1",ChatId = 1,Id = 1,Question = "Question1"},
            new Message {Answer = "Answer2",ChatId = 1,Id = 2,Question = "Question2"},
            new Message {Answer = "Answer3",ChatId = 1,Id = 3,Question = "Question3"},
            new Message {Answer = "Answer4",ChatId = 1,Id = 4,Question = "Question4"},
            new Message {Answer = "Answer5",ChatId = 2,Id = 5,Question = "Question5"}];
        _dbContext.CreateDbSetMock(x => x.Messages, initialData);
        
        
        //Act
        var response = await _controller.GetMessageHistory(1);
        var result = Assert.IsType<OkObjectResult>(response);
        Assert.NotNull(result);
        var data = (object[])result.Value!;
        Assert.Equal(4,data.Length);
    }
    
    [Fact]
    public async Task GetMessageHistory_Should_ReturnEmpty_When_ThereAreNoMessages()
    {
        //Arrange
        List<Message> initialData = [];
        _dbContext.CreateDbSetMock(x => x.Messages, initialData);
        
        //Act
        var response = await _controller.GetMessageHistory(1);
        Assert.IsType<NoContentResult>(response);
    }
    
}