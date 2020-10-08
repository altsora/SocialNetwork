package sn.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PersonResponse extends AbstractResponse {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("first_name")
    private final String firstName;
    @JsonProperty("last_name")
    private final String lastName;
    @JsonProperty("reg_date")
    private final Long regDate;
    @JsonProperty("birth_date")
    private final Long birthDate;
    @JsonProperty("email")
    private final String email;
    @JsonProperty("phone")
    private final String phone;
    @JsonProperty("photo")
    private final String photo;
    @JsonProperty("about")
    private final String about;
    @JsonProperty("city")
    private final String city;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("messages_permission")
    private final String messagesPermission;
    @JsonProperty("last_online_time")
    private final Long lastOnlineTime;
    @JsonProperty("is_blocked")
    private final boolean isBlocked;
}
