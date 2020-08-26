package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sn.model.enums.FriendshipStatusCode;

@Getter
@Builder
@RequiredArgsConstructor
public class IsFriendResponse extends AbstractResponse {

    @JsonProperty("user_id")
    private final long id;

    @JsonProperty("status")
    private final FriendshipStatusCode statusCode;
}
