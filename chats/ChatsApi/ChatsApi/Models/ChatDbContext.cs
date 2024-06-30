using Microsoft.EntityFrameworkCore;

namespace ChatsApi.Models;


//constructor para opciones
public class ChatDbContext(DbContextOptions<ChatDbContext> options) : DbContext(options)
{
   public DbSet<Chat> Chats { get; set; }


    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {

        modelBuilder.Entity<Chat>().HasKey(chat => chat.ChatId);
        modelBuilder.Entity<Chat>().HasIndex(chat => chat.UserId);


    }
}