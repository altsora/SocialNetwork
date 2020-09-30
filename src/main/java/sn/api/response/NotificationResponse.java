package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sn.model.NotificationType;
import sn.model.Person;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Andrey.Kazakov
 * @date 21.09.2020
 */
@Getter
@Builder
public class NotificationResponse extends AbstractResponse {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("type_id")
    private final long typeId;
    @JsonProperty("sent_time")
    private final long sentTime;
    @JsonProperty("entity_id")
    private final long entityId;
    @JsonProperty("info")
    private final String info;
}
