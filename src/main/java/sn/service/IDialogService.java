package sn.service;

import sn.api.requests.MessageSendRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.UserActivityResponse;
import org.springframework.http.ResponseEntity;
import sn.api.response.DialogResponse;
import sn.api.response.ServiceResponse;
import sn.controller.DialogController;
import sn.model.Dialog;

public interface IDialogService {

    Dialog findById(long dialogId);

    boolean exists(long dialogId);

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

    ResponseEntity<ServiceResponse<AbstractResponse>> readMessage(long dialogId, long messageId);

    ResponseEntity<ServiceResponse<AbstractResponse>> getLastActivity(long dialogId, long personId);

    ResponseEntity<ServiceResponse<AbstractResponse>> changeTypingStatus(long dialogId, long personId);

    ResponseEntity<ServiceResponse<AbstractResponse>> sendMessage(long dialogId, MessageSendRequest sendRequest);

    ResponseEntity<ServiceResponse<AbstractResponse>> removeMessage(long dialogId, long messageId);

    ResponseEntity<ServiceResponse<AbstractResponse>> editMessage(long dialogId, long messageId,
                                                                  MessageSendRequest messageSendRequest);

    ResponseEntity<ServiceResponse<AbstractResponse>> recoverMessage(long dialogId, long messageId);


}
