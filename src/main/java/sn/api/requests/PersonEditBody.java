package sn.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonEditBody {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("birth_date")
    private long birthDate;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("photo_id")
    private String photoId;
    @JsonProperty("about")
    private String about;
    @JsonProperty("town_id")
    private int townId;
    @JsonProperty("country_id")
    private int countryId;
    @JsonProperty("messages_permission")
    private String messagesPermission;
}
