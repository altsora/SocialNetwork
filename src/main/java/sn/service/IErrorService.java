package sn.service;

import sn.api.response.ErrorResponse;

public interface IErrorService {
    ErrorResponse userNotFoundInDialog(long personId, long dialogId);

    ErrorResponse dialogNotFound(long dialogId);

    ErrorResponse personNotFoundById(long personId);

    ErrorResponse unauthorized();

    ErrorResponse messageNotFound(long messageId);

    ErrorResponse unknownLikeType(String likeType);
}
