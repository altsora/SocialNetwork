package sn.service.impl;

import org.springframework.stereotype.Service;
import sn.api.response.ErrorResponse;
import sn.service.IErrorService;

@Service
public class ErrorService implements IErrorService {
    private static final String BAD_REQUEST = "Bad request";
    private static final String INVALID_REQUEST = "Invalid request";

    @Override
    public ErrorResponse userNotFoundInDialog(long personId, long dialogId) {
        return ErrorResponse.builder()
                .error(BAD_REQUEST)
                .errorDescription(String.format("User (ID = %d) not found in dialog (ID = %d)", personId, dialogId))
                .build();
    }

    @Override
    public ErrorResponse dialogNotFound(long dialogId) {
        return ErrorResponse.builder()
                .error(BAD_REQUEST)
                .errorDescription("Dialog with ID = " + dialogId + " not found")
                .build();
    }

    @Override
    public ErrorResponse personNotFoundById(long personId) {
        return ErrorResponse.builder()
                .error(BAD_REQUEST)
                .errorDescription("Person with ID = " + personId + " not found")
                .build();
    }

    @Override
    public ErrorResponse unauthorized() {
        return ErrorResponse.builder()
                .error(INVALID_REQUEST)
                .errorDescription("Person not authorized")
                .build();
    }

    @Override
    public ErrorResponse messageNotFound(long messageId) {
        return ErrorResponse.builder()
                .error(BAD_REQUEST)
                .errorDescription("Message with ID = " + messageId + " not found")
                .build();
    }

    @Override
    public ErrorResponse unknownLikeType(String likeType) {
        return ErrorResponse.builder()
                .error(BAD_REQUEST)
                .errorDescription("Unknown like type: " + likeType)
                .build();
    }


}
