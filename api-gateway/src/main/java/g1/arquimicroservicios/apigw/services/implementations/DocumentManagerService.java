package g1.arquimicroservicios.apigw.services.implementations;

import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@Service
public class DocumentManagerService implements IDocumentManagerService {




    @Value("${document-manager.api}")
    private String url;

    private final RestTemplate restClient;

    public DocumentManagerService(RestTemplate restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<Boolean> uploadFile(MultipartFile file) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // Create a map to hold the parts of the request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileAsResource);
            body.add("title", file.getOriginalFilename());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<?> response = restClient.postForEntity(url + "/documents", requestEntity, Void.class);

            return Optional.of(response.getStatusCode().is2xxSuccessful());
        } catch (IOException e){
            e.printStackTrace();
            return  Optional.empty();
        }

    }
}
