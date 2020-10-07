package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.service.StorageService;

import javax.mail.Multipart;

/**
 * Класс StorageController.
 * Реализация API для закгрузки картинок.
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    /**
     * Метод uploadFile.
     * Загрузка картинок.
     * MULTIPART FORM-DATA POST запрос /api/v1/storage
     *
     * @param file multipart form-data файл
     * @param type тип файла
     * @return ResponseEntity<ServiceResponse <AbstractResponse>>
     * @see sn.api.response.FileUploadResponse
     * @see StorageService
     */
    @PostMapping
    public ResponseEntity<ServiceResponse<AbstractResponse>> uploadFile(@RequestParam(required = false) MultipartFile file,
                                                                        @RequestParam(required = false) String type) {
        return storageService.uploadFile(file, type);
    }
}
