package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.response.AbstractResponse;
import sn.service.StorageService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping(value = "storage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam String type,
                                     @RequestParam(value = "file", required = false)
                                     MultipartFile file) throws IOException {
        AbstractResponse response = new AbstractResponse(storageService, type, file);
        Map<String, Object> uploadResult = response.uploadFileResponse();
        return new ResponseEntity(uploadResult, response.isSuccess() ? HttpStatus.OK :
                HttpStatus.BAD_REQUEST);
    }

//        data.setRawFileURL(map.get("url").toString());
}
