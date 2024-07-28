using ChatsApi.Controllers;
using ChatsApi.Controllers.Dtos;
using ChatsApi.Models;
using ChatsApi.ServicesInterface;
using Microsoft.AspNetCore.Mvc;
using Moq;

namespace TestProject1;

public class ChatsControllerTests
{
    private readonly Mock<IChatsService> _mockChatsService;
    private readonly ChatsController _controller;

    public ChatsControllerTests()
    {
        _mockChatsService = new Mock<IChatsService>();
        _controller = new ChatsController(_mockChatsService.Object);
    }

    [Fact]
    public async Task GetUserChats_ReturnsNoContent_WhenNoChats()
    {
        // Arrange
        int userId = 1;
        _mockChatsService.Setup(service => service.GetUser(userId)).ReturnsAsync(new List<Chat>());

        // Act
        var result = await _controller.GetUserChats(userId);

        // Assert
        Assert.IsType<NoContentResult>(result);

        // Verify
        _mockChatsService.Verify(service => service.GetUser(userId), Times.Once);
    }

    [Fact]
    public async Task GetUserChats_ReturnsOk_WithChats()
    {
        // Arrange
        int userId = 1;
        var chats = new List<Chat>
        {
            new() { ChatId = 1, ChatName = "Chat1" }, new() { ChatId = 2, ChatName = "Chat2" }
        };

        _mockChatsService.Setup(service => service.GetUser(userId)).ReturnsAsync(chats);

        // Act
        var result = await _controller.GetUserChats(userId);

        // Assert
        var okResult = Assert.IsType<OkObjectResult>(result);
        var returnValue = Assert.IsType<List<ChatDto>>(okResult.Value);
        Assert.Equal(2, returnValue.Count);
        Assert.Equal("Chat1", returnValue[0].ChatName);
        Assert.Equal("Chat2", returnValue[1].ChatName);

        // Verify
        _mockChatsService.Verify(service => service.GetUser(userId), Times.Once);
    }

    [Fact]
    public async Task AddUserChat_ReturnsConflict_WhenChatAlreadyExists()
    {
        // Arrange
        int userId = 1;
        string chatName = "Chat1";

        _mockChatsService.Setup(service => service.AddChat(userId, chatName)).ReturnsAsync((Chat?)null);

        // Act
        var result = await _controller.AddUserChat(userId, new CreateChatDto(chatName));

        // Assert
        var conflictResult = Assert.IsType<ConflictObjectResult>(result);
        Assert.Equal("Chat with given name already exists", conflictResult.Value);

        // Verify
        _mockChatsService.Verify(service => service.AddChat(userId, chatName), Times.Once);
    }

    [Fact]
    public async Task AddUserChat_ReturnsCreated_WhenChatAdded()
    {
        // Arrange
        int userId = 1;
        string chatName = "Chat1";

        var chat = new Chat { ChatId = 1, ChatName = chatName };

        _mockChatsService.Setup(service => service.AddChat(userId, chatName)).ReturnsAsync(chat);

        // Act
        var result = await _controller.AddUserChat(userId, new CreateChatDto(chatName));

        // Assert
        Assert.IsType<CreatedAtActionResult>(result);

        // Verify
        _mockChatsService.Verify(service => service.AddChat(userId, chatName), Times.Once);
    }
}