package sn.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import sn.api.response.AbstractResponse;

@Data
@AllArgsConstructor
public class ResponseDataMessage extends AbstractResponse {
    private String message;
}
