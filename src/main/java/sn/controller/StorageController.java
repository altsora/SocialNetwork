package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.api.response.AbstractResponse;
import sn.api.response.StorageResponse;
import sn.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping//(value = "storage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam String type,
                                     @RequestParam(value = "file", required = false)
                                     MultipartFile file) throws IOException {
        AbstractResponse response = storageService.uploadFileResponse(type, file);
        // Если нужно получить сслыку на файл в хранилище, то используем это:
        // ((StorageResponse) response).getResponse().get("url")
        return new ResponseEntity(response, ((StorageResponse) response).isSuccess()
                ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
