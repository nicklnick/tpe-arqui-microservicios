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

import java.util.Optional;


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

        Optional<Boolean> maybeCreated = documentManagerService.uploadFile(file);

        if (maybeCreated.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }

        boolean created = maybeCreated.get();
        if (!created){
            // TODO NICO  el error seria que el archivo ya exista?
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();


    }
}
