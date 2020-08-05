package sn.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sn.service.StorageService;

import java.io.IOException;
import java.util.Map;

@Getter
@Setter
public class AbstractResponse {
    private StorageService service;
    private String type;
    private MultipartFile file;

    private boolean success = false;

    public AbstractResponse(StorageService service, String type, MultipartFile file) {
        this.service = service;
        this.type = type;
        this.file = file;
    }

    public Map<String, Object> uploadFileResponse() throws IOException {
        Map<String, Object> answer = service.uploadFileResponse(type, file);
        if (answer != null) {
            success = true;
        }
        return answer;
    }
}
