package g1.arquimicroservicios.apigw.services.implementations;

import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.DocumentDataResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
    public ServiceResponse<Integer,HttpStatus> uploadFile(MultipartFile file) {

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

            ResponseEntity<?> response = restClient.postForEntity(url + "/api/documents", requestEntity, Void.class);
            String location = response.getHeaders().getFirst("Location");
            if (location == null) {
                throw new RuntimeException("Missing header: Location");
            }
            String[] payload = location.split("/");
            int documentId = Integer.parseInt(payload[payload.length - 1]);
            return new ServiceResponse<>(documentId,HttpStatus.CREATED);
        } catch (HttpClientErrorException | HttpServerErrorException e){
            return new ServiceResponse<>(null,HttpStatus.valueOf(e.getStatusCode().value()));
        } catch ( IOException e) {
            return new ServiceResponse<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ServiceResponse<List<DocumentDataResponseDto>, HttpStatus> getData(int page, int pageSize)
    {
        try {
            ResponseEntity<List<DocumentDataResponseDto>> response = restClient.exchange(UriComponentsBuilder.fromHttpUrl(url)
                    .path("/api/documents")
                    .queryParam("page_size", pageSize)
                    .queryParam("page", page)
                    .build()
                    .toUri(), HttpMethod.GET,null, new ParameterizedTypeReference<>() {
            });
            if (response.getStatusCode() == HttpStatus.NO_CONTENT){
                return new ServiceResponse<>(Collections.emptyList(),HttpStatus.NO_CONTENT);
            }
            return new ServiceResponse<>(response.getBody(),HttpStatus.OK);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            exception.printStackTrace();
            return new ServiceResponse<>(null,HttpStatus.valueOf(exception.getStatusCode().value()));
        }
    }

    public ServiceResponse<byte[], HttpStatus> getDocument(int documentId){

        try {
            ResponseEntity<byte[]> response = restClient.getForEntity(UriComponentsBuilder.fromHttpUrl(url)
                    .path("/api/documents/{id}").buildAndExpand(documentId).toUri(),byte[].class);
            return new ServiceResponse<>(response.getBody(),HttpStatus.OK);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
            return new ServiceResponse<>(null,HttpStatus.valueOf(e.getStatusCode().value()));
        }


    }
}
