package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@Builder
@RequiredArgsConstructor
public class TagResponse extends AbstractResponse{

    @JsonProperty("id")
    private final long id;

    @JsonProperty("tag")
    private final String tag;
}
