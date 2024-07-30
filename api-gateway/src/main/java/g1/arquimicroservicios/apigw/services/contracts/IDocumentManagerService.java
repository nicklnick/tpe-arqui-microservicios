package g1.arquimicroservicios.apigw.services.contracts;

import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.DocumentDataResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IDocumentManagerService {
    ServiceResponse<Integer,HttpStatus> uploadFile(MultipartFile file);


    ServiceResponse<List<DocumentDataResponseDto>, HttpStatus> getData(int page, int pageSize);

    ServiceResponse<byte[], HttpStatus> getDocument(int documentId);
}
