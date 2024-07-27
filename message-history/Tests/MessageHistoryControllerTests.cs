using Api.Controllers;
using Api.Models;
using Api.ServicesImplementation;
using Api.ServicesInterfaces;
using Microsoft.AspNetCore.Mvc;
using Moq;

namespace Tests;

public class MessageHistoryControllerTests
{
    private readonly MessagesController _controller;
    private readonly Mock<IMessageHistoryService> _messageHistoryMock;

    public MessageHistoryControllerTests()
    {
        _messageHistoryMock = new Mock<IMessageHistoryService>();
        _controller = new MessagesController(_messageHistoryMock.Object);
    }
    

    [Fact]
    public async Task GetMessageHistory_Should_ReturnNoContent_When_NoMessagesFound()
    {
        // Arrange
        _messageHistoryMock.Setup(service => service.FindMessagesForChat(It.IsAny<int>(), It.IsAny<int>(), It.IsAny<int>()))
            .ReturnsAsync([]);

        // Act
        var result = await _controller.GetMessageHistory(1);

        // Assert
        Assert.IsType<NoContentResult>(result);
    }

    [Fact]
    public async Task GetMessageHistory_Should_ReturnOk_When_MessagesFound()
    {
        // Arrange
        var messages = new[]
        {
            new Message { Id = 1, Question = "Question1", Answer = "Answer1", ChatId = 1},
            new Message { Id = 2, Question = "Question2", Answer = "Answer2" , ChatId = 1}
        };

        _messageHistoryMock.Setup(service => service.FindMessagesForChat(1, It.IsAny<int>(), It.IsAny<int>()))
            .ReturnsAsync(messages);

        // Act
        var result = await _controller.GetMessageHistory(1);

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result);
        var returnValue = (object[]) okResult.Value!;
        Assert.Equal(2, returnValue.Length);
        Assert.Collection(returnValue,
            item => Assert.Contains("Question1", item.ToString()),
            item => Assert.Contains("Question2", item.ToString())
        );
    }
    
    
}