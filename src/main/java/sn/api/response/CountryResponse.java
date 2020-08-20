package sn.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CountryResponse {
    private final int id = 1;
    private final String title;
}
