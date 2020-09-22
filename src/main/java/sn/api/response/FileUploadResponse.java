package sn.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse extends AbstractResponse {
    String id;
    long ownerId;
    String fileName;
    String relativeFilePath;
    String rawFileURL;
    String fileFormat;
    long bytes;
    String fileType;
    long createdAt;
}
