package sn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulFileUpload {
    private String error;
    private Number timestamp;
    private FileUploadResponse data;
}
