package ar.edu.itba.arquimicro.ampqcontrollers.util;

public interface QUEUES_DATA {

    String DIRECT_EXCHANGE_NAME = "direct_exchange";
    String PROCESS_INPUT_QUEUE = "process_input";

    interface SEND_LLM_DATA {
        String SEND_LLM_QUEUE = "send_llm";
        String SEND_LLM_ROUTING_KEY = "send_llm_routing_key";
    }

    interface SEND_MESSAGE_HISTORY_DATA {
        String SEND_MESSAGE_HISTORY_QUEUE = "history_messages";

        String SEND_MESSAGE_HISTORY_ROUTING_KEY = "send_message_history_routing_key";
    }


    // from llm manager
    String RECEIVE_LLM = "receive_llm";

    // to llm manager
    // to message history
    String SAVE_RESPONSE = "save_response";
    // to api gw
    String SEND_RESPONSE_TO_USER = "send_response_to_user";


}
