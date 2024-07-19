package ar.edu.itba.arquimicro.ampq.util;

public class ServiceNames {
    public static final String API_GW = "api_gw";
    public static final String LLM_MANAGER = "llm_manager";
    public static final String MESSAGE_HISTORY = "message_history";

    private ServiceNames() {
        throw new AssertionError("This class should not be instantiated");
    }
}
