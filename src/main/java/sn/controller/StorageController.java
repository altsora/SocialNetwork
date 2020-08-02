package sn.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import sn.dto.FileType;
import sn.dto.FileUploadResponse;
import sn.dto.SuccessfulFileUpload;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    // Cloudinary cloud_name, API_Key and API_Secret
    private static final String CLOUDINARY_CLOUD_NAME = "javapro-socialnetwork-studygroup-7";
    private static final String CLOUDINARY_API_KEY = "751383328333813";
    private static final String CLOUDINARY_API_SECRET = "11mJVXn8IE-H5oftzDbYbycc3Ig";

    @PostMapping
    public SuccessfulFileUpload upload(@RequestParam String nameWithPath) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUDINARY_CLOUD_NAME,
                "api_key", CLOUDINARY_API_KEY,
                "api_secret", CLOUDINARY_API_SECRET));
        Map map =
                cloudinary.uploader().upload(nameWithPath, ObjectUtils.emptyMap());

        FileUploadResponse data = getData(map);
        SuccessfulFileUpload response = new SuccessfulFileUpload();
        response.setError("No errors!");
        response.setTimestamp((new Date()).getTime());
        response.setData(data);
        return response;
    }

    private FileUploadResponse getData(Map map) {
        FileUploadResponse data = new FileUploadResponse();
        data.setId("twelve");
        data.setOwnerId(12);
        data.setFileName(map.get("original_filename").toString());
        data.setRelativeFilePath(map.get("secure_url").toString());
        data.setRawFileURL(map.get("url").toString());
        data.setFileFormat(map.get("format").toString());
        data.setBytes(Integer.parseInt(map.get("bytes").toString()));
        data.setFileType(FileType.IMAGE);
        data.setCreatedAt(map.get("created_at").toString());
        return data;
    }
}
