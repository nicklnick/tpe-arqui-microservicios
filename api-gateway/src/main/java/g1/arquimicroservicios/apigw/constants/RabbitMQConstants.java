package g1.arquimicroservicios.apigw.constants;

public interface RabbitMQConstants {
    String QUESTION_QUEUE = "process_input";
    String QUESTION_EXCHANGE = "inputExchange";
    String QUESTION_ROUTING_KEY = "inputRoutingKey";

    String FANOUT_EXCHANGE_RESPONSE_LLM = "fanout_exchange_response_llm";
}
