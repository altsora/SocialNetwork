package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UserActivityResponse extends AbstractResponse {
    @JsonProperty("online")
    private final boolean online;
    @JsonProperty("last_activity")
    private final Long lastActivity;
}
