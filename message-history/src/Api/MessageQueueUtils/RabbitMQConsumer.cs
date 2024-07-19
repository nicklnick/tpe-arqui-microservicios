using System.Text;
using System.Text.Json;
using Api.ServicesInterfaces;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace Api.MessageQueueUtils;

public class RabbitMqConsumer : BackgroundService
{
    private IConfiguration _configuration;
    private IConnection _connection;
    private IModel _channel;
    private IServiceScopeFactory _scopeFactory;

    public RabbitMqConsumer(IConfiguration configuration, IServiceScopeFactory scope)
    {
        _scopeFactory = scope;
        _configuration = configuration;
        InitializeRabbitMqConsumer();
    }

    private void InitializeRabbitMqConsumer()
    {
        var factory = new ConnectionFactory
        {
            HostName = _configuration["RabbitMQ:Hostname"],
            UserName = _configuration["RabbitMQ:Username"],
            Password = _configuration["RabbitMQ:Password"]
        };

        _connection = factory.CreateConnection();
        _channel = _connection.CreateModel();

        _channel.QueueDeclare(queue: _configuration["RabbitMQ:QueueName"], durable: true, exclusive: false,
            autoDelete: false, arguments: null);


        var consumer = new EventingBasicConsumer(_channel);
        
        consumer.Received += async (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var message = Encoding.UTF8.GetString(body);
            var uploaded = await ProcessMessage(message);
            if (uploaded)
            {
                _channel.BasicAck(deliveryTag: ea.DeliveryTag, multiple: false);
                return;
            }
            _channel.BasicNack(deliveryTag: ea.DeliveryTag, multiple: false, requeue: false);

        };

        _channel.BasicConsume(queue: _configuration["RabbitMQ:QueueName"],
            autoAck: false,
            consumer: consumer);
    }


    private async Task<bool> ProcessMessage(string message)
    {
        using var scope = _scopeFactory.CreateScope();
        var messageHistoryService = scope.ServiceProvider.GetRequiredService<IMessageHistoryService>();
        var messageModel = JsonSerializer.Deserialize<QueueMessage>(message);
        var uploadMessageOk = await messageHistoryService.UploadMessage(messageModel!.Question, messageModel.Answer,messageModel.ChatId);
        return uploadMessageOk;
    }
    
    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        // No additional background tasks are required.
        return Task.CompletedTask;
    }

    public override void Dispose()
    {
        _channel?.Close();
        _connection?.Close();
        base.Dispose();
    }
    
}