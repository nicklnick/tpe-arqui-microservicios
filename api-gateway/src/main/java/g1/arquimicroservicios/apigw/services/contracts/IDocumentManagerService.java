package g1.arquimicroservicios.apigw.services.contracts;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface IDocumentManagerService {
    Optional<Boolean> uploadFile(MultipartFile file);
}
