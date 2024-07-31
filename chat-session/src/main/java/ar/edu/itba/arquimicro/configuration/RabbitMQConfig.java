package ar.edu.itba.arquimicro.configuration;

import ar.edu.itba.arquimicro.ampqcontrollers.util.QUEUES_DATA;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue processInput() {
        return new Queue(QUEUES_DATA.PROCESS_INPUT_QUEUE,false);
    }

    @Bean
    public Queue receiveLlm() {
        return new Queue(QUEUES_DATA.RECEIVE_LLM,true);
    }

    @Bean
    public Queue llmQueue() {
        return new Queue(QUEUES_DATA.SEND_LLM_DATA.SEND_LLM_QUEUE, true);
    }

    @Bean
    public Queue messageHistoryQueue() {
        return new Queue(QUEUES_DATA.SEND_MESSAGE_HISTORY_DATA.SEND_MESSAGE_HISTORY_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(QUEUES_DATA.DIRECT_EXCHANGE_NAME);
    }

    @Bean
    public Binding llmBinding(@Qualifier("llmQueue") Queue queue1, DirectExchange exchange) {
        return BindingBuilder.bind(queue1).to(exchange).with(QUEUES_DATA.SEND_LLM_DATA.SEND_LLM_ROUTING_KEY);
    }

    // for future queue
    @Bean
    public Binding messageHistoryBinding(@Qualifier("messageHistoryQueue") Queue queue2, DirectExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with(QUEUES_DATA.SEND_MESSAGE_HISTORY_DATA.SEND_MESSAGE_HISTORY_ROUTING_KEY);
    }


}
