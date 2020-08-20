package sn.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import sn.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseDataList<T extends AbstractResponse> {
    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private List<T> data;

    public ServiceResponseDataList(String error) {
        this.timestamp = TimeUtil.getTimestampFromLocalDateTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        this.error = error;
    }

    public ServiceResponseDataList(int total, int offset, int perPage, List<T> data) {
        this.timestamp = TimeUtil.getTimestampFromLocalDateTime(LocalDateTime.now(TimeUtil.TIME_ZONE));
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}
