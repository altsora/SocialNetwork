package sn.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsFriendsRequest {

    @JsonProperty("user_ids")
    List<Integer> userIds;
}
