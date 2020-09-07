package sn.service;

import org.springframework.http.ResponseEntity;
import sn.api.response.DialogResponse;
import sn.api.response.ServiceResponse;
import sn.controller.DialogController;
import sn.model.Dialog;

public interface IDialogService {

    Dialog findById(long dialogId);

    ResponseEntity<ServiceResponse<DialogResponse>> findPersonDialogsWithQuery(
            String query, int offSet, int itemPerPage);

    ResponseEntity<ServiceResponse<DialogResponse>> createDialog(DialogController.UserIdsRequest request);

    ResponseEntity<ServiceResponse<DialogResponse>> getUnreadMessagesCount();

    ResponseEntity<ServiceResponse<DialogResponse>> deleteDialog(long dialogId);

    ResponseEntity<ServiceResponse<DialogResponse>> addUsersToDialog(
            long dialogId, DialogController.UserIdsRequest request);

    ResponseEntity<ServiceResponse<DialogResponse>> deleteUsersFromDialog(
            long dialogId, DialogController.UserIdsRequest request);

    ResponseEntity<ServiceResponse<DialogResponse>> createInviteLink(long dialogId);

    ResponseEntity<ServiceResponse<DialogResponse>> joinUserToDialog(long dialogId, String link);

    ResponseEntity<ServiceResponse<DialogResponse>> getDialogMessages(
            long dialogId, String query, int offset, int itemPerPage);
}
