package sn.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeRequest {
    @JsonProperty("item_id")
    private long itemId;
    @JsonProperty("type")
    private String type;
}
