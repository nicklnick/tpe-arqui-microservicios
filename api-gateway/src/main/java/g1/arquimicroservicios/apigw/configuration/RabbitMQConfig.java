package g1.arquimicroservicios.apigw.configuration;

import g1.arquimicroservicios.apigw.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
    public FanoutExchange createFanoutExchange() {
        return new FanoutExchange(RabbitMQConstants.FANOUT_EXCHANGE_RESPONSE_LLM);
    }


    @Bean
    public AnonymousQueue llmConsumerQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding llmConsumerQueueBinding(FanoutExchange fanoutExchange, @Qualifier("llmConsumerQueue") Queue llmConsumerQueue) {
        return BindingBuilder.bind(llmConsumerQueue).to(fanoutExchange);
    }


    @Bean
    public Binding requestBinding(@Qualifier("requestQueue") Queue requestQueue, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue).to(exchange).with(RabbitMQConstants.QUESTION_ROUTING_KEY);
    }

}
