package sn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sn.api.response.AbstractResponse;
import sn.api.response.FileUploadResponse;
import sn.api.response.ServiceResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static sn.api.response.AbstractResponse.createErrorResponse;

/**
 * Класс StorageService.
 * Сервис для работы с Cloudinary и формирования ответа для StorageController.
 *
 * @see sn.controller.StorageController
 * @see sn.config.CloudinaryConfig
 */
@Slf4j
@Service
public class StorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private AccountService accountService;

    public ResponseEntity<ServiceResponse<AbstractResponse>> uploadFile(String type, String path) {
        if (!Strings.isNotEmpty(type) || !type.equals("IMAGE")) {
            log.error("type [{}] is incorrect", type);
            return createErrorResponse("incorrect type", "file type must be \"image\" or \"IMAGE\"");
        }
        try {
            Map map = cloudinary.uploader().upload(path, ObjectUtils.emptyMap());
            FileUploadResponse response = FileUploadResponse.builder()
                    .id((String) map.get("signature"))
                    .ownerId(accountService.findCurrentUser().getId())
                    .fileName((String) map.get("original_filename"))
                    .relativeFilePath((String) map.get("secure_url"))
                    .rawFileURL((String) map.get("secure_url"))
                    .fileFormat((String) map.get("format"))
                    .bytes((Integer) map.get("bytes"))
                    .fileType(type)
                    .createdAt(Instant.parse(((String) map.get("created_at"))).getEpochSecond())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(new ServiceResponse<>(response));
        } catch (IOException exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            return createErrorResponse("IO exception", exception.getMessage());
        }
    }
}
