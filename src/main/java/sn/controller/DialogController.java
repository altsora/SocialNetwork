package sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.requests.MessageSendRequest;
import sn.api.response.*;
import sn.model.Person;
import sn.service.IAccountService;
import sn.service.IDialogService;
import sn.service.IErrorService;
import sn.service.IMessageService;

@RestController
@RequestMapping("/dialogs")
public class DialogController {
    private final IAccountService accountService;
    private final IDialogService dialogService;
    private final IErrorService errorService;
    private final IMessageService messageService;

    @Autowired
    public DialogController(
            @Qualifier("account-service") IAccountService accountService,
            IDialogService dialogService,
            IErrorService errorService,
            IMessageService messageService) {
        this.accountService = accountService;
        this.dialogService = dialogService;
        this.errorService = errorService;
        this.messageService = messageService;
    }
    
    @PostMapping("/{id}/messages")
    public ResponseEntity<ServiceResponse<AbstractResponse>> sendMessage(
            @PathVariable("id") long dialogId,
            @RequestBody MessageSendRequest sendRequest
    ) {
        if (!dialogService.exists(dialogId)) {
            ErrorResponse errorResponse = errorService.dialogNotFound(dialogId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        Person author = accountService.findCurrentUser();
        if (author == null) {
            ErrorResponse errorResponse = errorService.unauthorized();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ServiceResponse<>(errorResponse));
        }
        MessageFullResponse messageFullResponse = messageService.sendMessage(author, dialogId, sendRequest.getMessageText());
        return ResponseEntity.ok(new ServiceResponse<>(messageFullResponse));
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> removeMessage(
            @PathVariable("dialog_id ") long dialogId,
            @PathVariable("message_id ") long messageId
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = errorService.messageNotFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        long id = messageService.removeMessage(messageId);
        MessageIdResponse messageIdResponse = MessageIdResponse.builder().messageId(id).build();
        return ResponseEntity.ok(new ServiceResponse<>(messageIdResponse));
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> editMessage(
            @PathVariable("dialog_id ") long dialogId,
            @PathVariable("message_id ") long messageId,
            @RequestBody MessageSendRequest messageSendRequest
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = errorService.messageNotFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        MessageFullResponse messageFullResponse = messageService.editMessage(messageId, messageSendRequest.getMessageText());
        return ResponseEntity.ok(new ServiceResponse<>(messageFullResponse));
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/recover")
    public ResponseEntity<ServiceResponse<AbstractResponse>> recoverMessage(
            @PathVariable("dialog_id") long dialogId,
            @PathVariable("message_id") long messageId
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = errorService.messageNotFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        MessageFullResponse messageFullResponse = messageService.recoverMessage(messageId);
        return ResponseEntity.ok(new ServiceResponse<>(messageFullResponse));
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    public ResponseEntity<ServiceResponse<AbstractResponse>> readMessage(
            @PathVariable("dialog_id") long dialogId,
            @PathVariable("message_id") long messageId
    ) {
        if (!messageService.exists(messageId)) {
            ErrorResponse errorResponse = errorService.messageNotFound(messageId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        if (!dialogService.exists(dialogId)) {
            ErrorResponse errorResponse = errorService.dialogNotFound(dialogId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        messageService.readMessage(messageId);
        //TODO: Если количество непрочитанных сообщений у всех пока что равно нулю,
        // то уменьшать не надо. Раскомментировать позже
//        dialogService.decreaseUnreadCount(dialogId);
        return ResponseEntity.ok(new ServiceResponse<>(ResponseDataMessage.ok()));
    }

    @GetMapping("/{id}/activity/{user_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> getLastActivity(
            @PathVariable("id") long dialogId,
            @PathVariable("user_id") long personId
    ) {
        if (!accountService.exists(personId)) {
            ErrorResponse errorResponse = errorService.personNotFoundById(personId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        if (!dialogService.exists(dialogId)) {
            ErrorResponse errorResponse = errorService.dialogNotFound(dialogId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        if (!dialogService.userExistsInDialog(personId, dialogId)) {
            ErrorResponse errorResponse = errorService.userNotFoundInDialog(personId, dialogId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        UserActivityResponse userActivityResponse = dialogService.getActivity(personId, dialogId);
        return ResponseEntity.ok(new ServiceResponse<>(userActivityResponse));
    }

    @PostMapping("/{id}/activity/{user_id}")
    public ResponseEntity<ServiceResponse<AbstractResponse>> changeTypingStatus(
            @PathVariable("id") long dialogId,
            @PathVariable("user_id") long personId
    ) {
        if (!accountService.exists(personId)) {
            ErrorResponse errorResponse = errorService.personNotFoundById(personId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        if (!dialogService.exists(dialogId)) {
            ErrorResponse errorResponse = errorService.dialogNotFound(dialogId);
            return ResponseEntity.badRequest().body(new ServiceResponse<>(errorResponse));
        }
        return ResponseEntity.ok(new ServiceResponse<>(ResponseDataMessage.ok()));
    }
}