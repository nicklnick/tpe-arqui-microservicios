package g1.arquimicroservicios.apigw.controllers;


import g1.arquimicroservicios.apigw.controllers.responseDtos.ApiDocumentDataResponseDto;
import g1.arquimicroservicios.apigw.services.contracts.IDocumentManagerService;
import g1.arquimicroservicios.apigw.services.implementations.genericServiceResponse.ServiceResponse;
import g1.arquimicroservicios.apigw.services.implementations.responseDtos.DocumentDataResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.net.URI;
import java.util.List;
import java.util.Map;


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
        ServiceResponse<Integer, HttpStatus> maybeCreated = documentManagerService.uploadFile(file);
        if (maybeCreated.httpStatusResponse().is2xxSuccessful()){
            return ResponseEntity.status(HttpStatus.CREATED).headers(headers -> headers.set("Location","/api/documents/" +maybeCreated.value()) ).body(Map.of("id",maybeCreated.value()));
        }
        return  ResponseEntity.status(maybeCreated.httpStatusResponse()).build();

    }

    @GetMapping
    public ResponseEntity<List<ApiDocumentDataResponseDto>> getCurrentDocuments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize)
    {
        if (page < 1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (pageSize < 1 || pageSize > 20){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ServiceResponse<List<DocumentDataResponseDto>,HttpStatus> serviceData =  documentManagerService.getData(page,pageSize);
        if (serviceData.httpStatusResponse().is2xxSuccessful()){
            return ResponseEntity.status(serviceData.httpStatusResponse()).body(serviceData.value().stream().map(data -> new ApiDocumentDataResponseDto(data.id(),data.title())).toList());
        }
        return ResponseEntity.status(serviceData.httpStatusResponse()).build();
    }

    @GetMapping("{documentId}")
    public ResponseEntity<byte[]> getSpecificDocument(@PathVariable("documentId") int documentId){
        ServiceResponse<byte[], HttpStatus> maybeDocument = documentManagerService.getDocument(documentId);
        if (maybeDocument.httpStatusResponse().is2xxSuccessful()){
            return ResponseEntity.status(maybeDocument.httpStatusResponse())
                    .headers(header -> header.setContentType(MediaType.APPLICATION_PDF))
                    .body(maybeDocument.value());
        }
        return  ResponseEntity.status(maybeDocument.httpStatusResponse()).build();
    }

}
