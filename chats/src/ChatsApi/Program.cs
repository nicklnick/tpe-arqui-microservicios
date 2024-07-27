using ChatsApi.Models;
using ChatsApi.ServicesImplementation;
using ChatsApi.ServicesInterface;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();
builder.Services.AddTransient<IChatsService, ChatsService>();


// Add your DbContext to the services
builder.Services.AddDbContext<ChatDbContext>(options => options.UseNpgsql(builder.Configuration.GetValue<string>("DB_CONNECTION")));

var app = builder.Build();
// Apply migrations at startup

using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;

    try
    {
        using (var context = services.GetRequiredService<ChatDbContext>())
        {
            context.Database.Migrate();
        }
    }
    catch (Exception ex)
    {
        var logger = services.GetRequiredService<ILogger<Program>>();
        logger.LogError(ex, "An error occurred while migrating the database.");
    }
}

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapControllers();

app.UseHttpsRedirection();

app.Run();