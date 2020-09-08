package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
public class MessageFullResponse extends AbstractResponse {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("time")
    private final Long time;
    @JsonProperty("author_id")
    private final long authorId;
    @JsonProperty("recipient_id")
    private final Long recipientId;
    @JsonProperty("message_text")
    private final String messageText;
    @JsonProperty("read_status")
    private final String readStatus;
}
