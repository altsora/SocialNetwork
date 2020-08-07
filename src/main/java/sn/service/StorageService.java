package sn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sn.api.response.StorageResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class StorageService {
    // Cloudinary cloud_name, API_Key and API_Secret
    private static final String
            CLOUDINARY_CLOUD_NAME = "javapro-socialnetwork-studygroup-7";
    private static final String
            CLOUDINARY_API_KEY = "751383328333813";
    private static final String
            CLOUDINARY_API_SECRET = "11mJVXn8IE-H5oftzDbYbycc3Ig";

    public StorageResponse uploadFileResponse(String type, MultipartFile file)
            throws IOException {
        StorageResponse cloudinaryUrl = new StorageResponse();
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUDINARY_CLOUD_NAME,
                "api_key", CLOUDINARY_API_KEY,
                "api_secret", CLOUDINARY_API_SECRET));
        // Convert multipart file type image to File type because Cloudinary
        // doesn't accept multipart file type.
        File convFile = multipartToFile(file);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result =
                    cloudinary.uploader().upload(convFile, ObjectUtils.emptyMap());
            cloudinaryUrl.setSuccess(true);
            cloudinaryUrl.setResponse(result);
        } catch (IOException e) {
            System.out.println("Could not upload file to Cloundinary from " +
                    "MultipartFile " + file.getOriginalFilename()+ e.toString());
            throw e;
        }
        return cloudinaryUrl;
    }

    private static File multipartToFile(MultipartFile image)
            throws IllegalStateException,
            IOException {
        File convFile = new File(Objects.
                requireNonNull(image.getOriginalFilename()));
        image.transferTo(convFile);
        return convFile;
    }
}
