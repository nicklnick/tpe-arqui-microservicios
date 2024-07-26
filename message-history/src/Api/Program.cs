using Api.EntityFrameworkConfig;
using Api.MessageQueueUtils;
using Api.ServicesImplementation;
using Api.ServicesInterfaces;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddDbContext<MessagesDbContext>(options =>
{
    options.UseNpgsql(builder.Configuration.GetValue<string>("DB_CONNECTION"));
});
builder.Services.AddTransient<IMessageHistoryService, MessageHistoryService>();
builder.Services.AddHostedService<RabbitMqConsumer>();
var app = builder.Build();


using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    try
    {
        var context = services.GetRequiredService<MessagesDbContext>();
        context.Database.Migrate();
    }
    catch (Exception e)
    {
        Console.WriteLine("Error ocurred while running migration");
    }
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();



app.Run();
