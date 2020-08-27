package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LikeCountResponse extends AbstractResponse {
    @JsonProperty("likes")
    private int likes;

    @JsonProperty("users")
    @Singular
    private List<Long> users;

    public LikeCountResponse(int likes) {
        this.likes = likes;
    }
}
