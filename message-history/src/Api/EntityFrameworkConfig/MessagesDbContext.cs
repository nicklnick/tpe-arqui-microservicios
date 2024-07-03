using Api.Models;
using Microsoft.EntityFrameworkCore;

namespace Api.EntityFrameworkConfig;

public class MessagesDbContext(DbContextOptions<MessagesDbContext> options) : DbContext(options)
{
    public DbSet<Message> Messages { get; set; }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Message>().HasKey(m => m.Id);
        modelBuilder.Entity<Message>().HasIndex(m => m.ChatId);
    }
}