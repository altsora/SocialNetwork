package sn.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CountryResponse {
    private final int id = 1;
    private final String title;
}
