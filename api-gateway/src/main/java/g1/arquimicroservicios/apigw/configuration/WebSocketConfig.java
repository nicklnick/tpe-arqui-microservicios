package g1.arquimicroservicios.apigw.configuration;

import g1.arquimicroservicios.apigw.controllers.websocket.WebSocketImplementation;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {


    private final WebSocketImplementation webSocketImplementation;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketImplementation, "api/ws")
                .setAllowedOrigins("*");
    }
}