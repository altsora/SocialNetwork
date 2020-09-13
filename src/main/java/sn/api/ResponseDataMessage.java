package sn.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import sn.api.response.AbstractResponse;

@Data
public class ResponseDataMessage extends AbstractResponse {
    private String message;

    public ResponseDataMessage(String message) {
        this.message = message;
    }
}


