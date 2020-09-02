package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sn.model.enums.StatusWallPost;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class WallPostResponse extends AbstractResponse {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("time")
    private final Long time;
    @JsonProperty("author")
    private final PersonResponse author;
    @JsonProperty("title")
    private final String title;
    @JsonProperty("post_text")
    private final String postText;
    @JsonProperty("is_blocked")
    private final boolean isBlocked;
    @JsonProperty("likes")
    private final int likesCount;
    @JsonProperty("comments")
    private final List<CommentResponse> comments;
    @JsonProperty("type")
    private final StatusWallPost statusWallPost;
}
