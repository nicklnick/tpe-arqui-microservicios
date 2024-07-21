package g1.arquimicroservicios.apigw.configuration;

import g1.arquimicroservicios.apigw.constants.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue requestQueue() {
        return new Queue(RabbitMQConstants.QUESTION_QUEUE, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstants.QUESTION_EXCHANGE);
    }

    @Bean
    public Binding requestBinding(Queue requestQueue, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue).to(exchange).with(RabbitMQConstants.QUESTION_ROUTING_KEY);
    }

}
