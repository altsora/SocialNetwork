package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.service.StorageService;

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
     * POST запрос /api/v1/storage
     *
     * @param type тип файла
     * @param path путь к файлу
     * @return ResponseEntity<ServiceResponse < AbstractResponse>>
     * @see sn.api.response.FileUploadResponse
     * @see StorageService
     */
    @PostMapping
    public ResponseEntity<ServiceResponse<AbstractResponse>> uploadFile(@RequestParam(required = false) String type,
                                                                        @RequestParam(required = false) String path) {
        return storageService.uploadFile(type, path);
    }
}
