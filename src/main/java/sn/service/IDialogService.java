package sn.service;

import sn.api.response.ErrorResponse;
import sn.api.response.UserActivityResponse;
import sn.model.Dialog;

public interface IDialogService {
    Dialog findById(long dialogId);

    boolean exists(long dialogId);

    boolean userExistsInDialog(long personId, long dialogId);

    void decreaseUnreadCount(long dialogId);

    UserActivityResponse getActivity(long personId, long dialogId);

    ErrorResponse dialogNotFoundResponse(long dialogId);

    ErrorResponse userNotFoundInDialogResponse(long personId, long dialogId);
}
