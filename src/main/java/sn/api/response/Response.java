package sn.api.response;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Response {
    private String error;
    private Number timestamp;
    private Object data;


    public Response() {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
    }
}
