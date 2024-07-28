package g1.arquimicroservicios.apigw.services.Injection;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

@Component
public class RestClientInjection {

    @Bean
    HttpClient registerHttpClient(){
        return HttpClient.newHttpClient();
    }

    @Bean
    RestTemplate registerRestClient(){
        return new RestTemplate();
    }
}
