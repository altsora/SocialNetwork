package sn.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse extends AbstractResponse{
    private String error;
    //TODO: добавить error_description
}
