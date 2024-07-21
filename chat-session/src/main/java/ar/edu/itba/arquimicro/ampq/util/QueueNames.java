package ar.edu.itba.arquimicro.ampq.util;

public class QueueNames {
    // from api gw

    public static final String PROCESS_INPUT = "process_input";
    // from llm manager
    public static final String RECEIVE_LLM = "receive_llm";

    // to llm manager
    public static final String SEND_LLM = "send_llm";
    // to message history
    public static final String SAVE_RESPONSE = "save_response";
    // to api gw
    public static final String SEND_RESPONSE_TO_USER = "send_response_to_user";

    private QueueNames() {
        throw new AssertionError("This class should not be instantiated");
    }
}
