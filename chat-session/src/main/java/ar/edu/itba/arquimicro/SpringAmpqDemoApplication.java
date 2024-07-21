package ar.edu.itba.arquimicro;

import ar.edu.itba.arquimicro.ampq.util.ServiceNames;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class SpringAmpqDemoApplication {

    private static final RestClient restClient = RestClient.builder()
            .baseUrl(ServiceNames.MESSAGE_HISTORY)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON.toString())
            .build();

    public static void main(String[] args) {
        SpringApplication.run(SpringAmpqDemoApplication.class, args);
    }

    @Bean
    RestClient restClient() {
        // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#_creating_a_restclient
        // "Once created (or built), the RestClient can be used safely by multiple threads."
        return restClient;
    }
}
