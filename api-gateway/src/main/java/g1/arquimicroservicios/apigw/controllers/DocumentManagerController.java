package g1.arquimicroservicios.apigw.controllers;


import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@RestController
@RequestMapping("/api/documents")
public class DocumentManagerController {

    private final IDocumentManagerService documentManagerService;

    @PostMapping
    public ResponseEntity<?> uploadPdfDocument(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }
        if (!"application/pdf".equals(file.getContentType())){
            return ResponseEntity.badRequest().body("Only pdf files are allowed");
        }

        boolean created = documentManagerService.uploadFile(file);
        if (!created){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();


    }
}
