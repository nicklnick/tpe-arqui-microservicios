using ChatsApi.Models;
using ChatsApi.ServicesImplementation;
using Microsoft.EntityFrameworkCore;
using Moq;
using Xunit;
using System.Threading.Tasks;
using EntityFrameworkCoreMock;

namespace TestProject1;

public class ChatServiceUnitTests
{
    
    [Fact]
    public async Task AddChat_ShouldReturnNull_WhenChatWithSameNameExists()
    {
        // Arrange
        var dbContextOptions = new DbContextOptionsBuilder<ChatDbContext>().Options;

        // Mock the DbContext
        var dbContextMock = new DbContextMock<ChatDbContext>(dbContextOptions);

        // Initial data for the DbSet
        var initialData = new List<Chat>
        {
            new() { UserId = 1, ChatName = "Chat1", ChatId = 1 },
            new() { UserId = 1, ChatName = "Chat2", ChatId = 2 },
            new() { UserId = 2, ChatName = "Chat1u2", ChatId = 3 }
        };

        // Mock the DbSet
        dbContextMock.CreateDbSetMock(x => x.Chats, initialData);

        // Create service instance with mocked DbContext
        var service = new ChatsService(dbContextMock.Object);

        // Act
        var result = await service.AddChat(1, "Chat1");

        // Assert
        Assert.Null(result);
    }

    [Fact]
    public async Task AddChat_ShouldAddNewChat_WhenNoChatWithSameNameExists()
    {
        // Arrange
        var dbContextOptions = new DbContextOptionsBuilder<ChatDbContext>().Options;

        // Mock the DbContext
        var dbContextMock = new DbContextMock<ChatDbContext>(dbContextOptions);

        // Initial data for the DbSet
        var initialData = new List<Chat>
        {
            new() { UserId = 1, ChatName = "Chat2", ChatId = 2 },
            new() { UserId = 2, ChatName = "Chat1u2", ChatId = 3 }
        };

        // Mock the DbSet
        var chatsMock = dbContextMock.CreateDbSetMock(x => x.Chats, initialData);
        // Create service instance with mocked DbContext
        var service = new ChatsService(dbContextMock.Object);
        // Act
        var result = await service.AddChat(1, "Chat1");

        // Assert
        Assert.NotNull(result);
        Assert.Equal("Chat1", result.ChatName);
        Assert.Equal(1, result.UserId);
        Assert.Equal(3, chatsMock.Object.Count());
    }

    [Fact]
    public async Task GetUser_ShouldReturnUserChats_WhenChatsExistForUser()
    {
        // Arrange
        var dbContextOptions = new DbContextOptionsBuilder<ChatDbContext>().Options;

        // Mock the DbContext
        var dbContextMock = new DbContextMock<ChatDbContext>(dbContextOptions);

        // Initial data for the DbSet
        var initialData = new List<Chat>
        {
            new Chat { UserId = 1, ChatName = "Chat1", ChatId = 1 },
            new Chat { UserId = 1, ChatName = "Chat2", ChatId = 2 },
            new Chat { UserId = 2, ChatName = "Chat1u2", ChatId = 3 }
        };

        // Mock the DbSet
        dbContextMock.CreateDbSetMock(x => x.Chats, initialData);

        // Create service instance with mocked DbContext
        var service = new ChatsService(dbContextMock.Object);

        // Act
        var result = await service.GetUser(1);

        // Assert
        Assert.NotNull(result);
        Assert.Equal(2, result.Count);
        Assert.Contains(result, chat => chat.ChatName == "Chat1" && chat.UserId == 1);
        Assert.Contains(result, chat => chat.ChatName == "Chat2" && chat.UserId == 1);
    }

    [Fact]
    public async Task GetUser_ShouldReturnEmptyList_WhenNoChatsExistForUser()
    {
        // Arrange
        var dbContextOptions = new DbContextOptionsBuilder<ChatDbContext>().Options;

        // Mock the DbContext
        var dbContextMock = new DbContextMock<ChatDbContext>(dbContextOptions);

        // Initial data for the DbSet
        var initialData = new List<Chat> { new () { UserId = 2, ChatName = "Chat1u2", ChatId = 3 } };

        // Mock the DbSet
        dbContextMock.CreateDbSetMock(x => x.Chats, initialData);

        // Create service instance with mocked DbContext
        var service = new ChatsService(dbContextMock.Object);

        // Act
        var result = await service.GetUser(1);

        // Assert
        Assert.NotNull(result);
        Assert.Empty(result);
    }
}