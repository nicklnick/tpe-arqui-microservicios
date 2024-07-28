package g1.arquimicroservicios.apigw.services.contracts;

import org.springframework.web.multipart.MultipartFile;

public interface IDocumentManagerService {
    boolean uploadFile(MultipartFile file);
}
