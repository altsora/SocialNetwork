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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.response.DialogResponse;
import sn.api.response.ServiceResponse;
import sn.service.IDialogService;

import java.util.List;

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

    @Autowired
    @Qualifier("dialogService")
    private IDialogService dialogService;

    /**
     * Метод getUserDialogList().
     * Получение списка диалогов авторизованного пользователя.
     * GET запрос /api/v1/dialogs.
     *
     * @param query       - строка для поиска (поиск как части в Message.messageText).
     * @param offset      - отступ от начала списка.
     * @param itemPerPage - количество диалогов на страницу.
     * @return список диалогов пользователя в json формате
     */
    @GetMapping
    public ResponseEntity<ServiceResponse<DialogResponse>> getUserDialogList(@RequestParam(required = false) String query,
                                                                             @RequestParam int offset,
                                                                             @RequestParam int itemPerPage) {
        return dialogService.findPersonDialogsWithQuery(query, offset, itemPerPage);
    }

    /**
     * Метод createDialog().
     * Создание диалога.
     * POST запрос /api/v1/dialogs.
     *
     * @param request - список id получателей (собеседников)
     * @return id созданного диалога в json формате
     */
    @PostMapping
    public ResponseEntity<ServiceResponse<DialogResponse>> createDialog(@RequestBody UserIdsRequest request) {
        return dialogService.createDialog(request);
    }


    /**
     * Метод getUnreadedMessagesCount().
     * Успешное получение общего кол-ва непрочитанных сообщений
     * GET запрос /api/v1/dialogs/unreaded.
     *
     * @return общее кол-во непрочитанных сообщений пользователя.
     */
    @GetMapping("/unreaded")
    public ResponseEntity<ServiceResponse<DialogResponse>> getUnreadedMessagesCount() {
        return dialogService.getUnreadMessagesCount();
    }

    /**
     * Метод deleteDialogById().
     * Удаление свзяи текущего пользователя и диалога с указанным id.
     * DELETE запрос /api/v1/dialogs/{id}.
     *
     * @return id отвязанного диалога.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<DialogResponse>> deleteDialogById(@PathVariable("id") long dialogId) {
        return dialogService.deleteDialog(dialogId);
    }

    /**
     * Метод addUsers().
     * Добавить пользователей в диалог.
     * PUT запрос /api/v1/dialogs/{id}/users.
     *
     * @return список id добавленных пользователей.
     */
    @PutMapping("/{id}/users")
    public ResponseEntity<ServiceResponse<DialogResponse>> addUsers(@PathVariable("id") long dialogId,
                                                                    @RequestBody UserIdsRequest request) {
        return dialogService.addUsersToDialog(dialogId, request);
    }


    /**
     * Метод deleteUsers().
     * Удалить пользователей из диалога.
     * DELETE запрос /api/v1/dialogs/{id}/users.
     *
     * @return список id добавленных пользователей.
     */
    @DeleteMapping("/{id}/users")
    public ResponseEntity<ServiceResponse<DialogResponse>> deleteUsers(@PathVariable("id") long dialogId,
                                                                       @RequestBody UserIdsRequest request) {
        return dialogService.deleteUsersFromDialog(dialogId, request);
    }

    /**
     * Метод invite.
     * Создание ссылки-приглашения в диалог.
     * GET запрос api/v1/dialogs/{id}/users/invite
     *
     * @param dialogId - Id диалога.
     * @return ответ, содержащий ссылку-приглашение.
     */
    @GetMapping("/{id}/users/invite")
    public ResponseEntity<ServiceResponse<DialogResponse>> invite(@PathVariable("id") long dialogId) {
        return dialogService.createInviteLink(dialogId);
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
    /**
     * Метод joinToDialog.
     * Присоедениться к диалогу по ссылке-приглашению.
     * PUT запрос /api/v1/dialogs/{id}/users/join/
     *
     * @param dialogId id диалога, к которому будем присоединяться.
     * @param link     inviteCode для сверки.
     * @return коллекция Id присоединенных к диалогу пользователей.
     */
    @PutMapping("/{id}/users/join")
    public ResponseEntity<ServiceResponse<DialogResponse>> joinToDialog(@PathVariable long dialogId,
                                                                        @RequestBody String link) {
        return dialogService.joinUserToDialog(dialogId, link);
    }

    /**
     * Метод getMessages.
     * Получение списка сообщений диалога.
     * GET запрос /api/v1/dialogs/{id}/messages
     * @param dialogId    - Id диалога.
     * @param query       - строка для поиска (поиск как части в Message.messageText).
     * @param offset      - смещение от начала списка сообщений.
     * @param itemPerPage - количество сообщений на страницу.
     * @return список сообщений.
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<ServiceResponse<DialogResponse>> getMessages(@PathVariable long dialogId,
                                                                       @RequestParam(required = false) String query,
                                                                       @RequestParam int offset,
                                                                       @RequestParam int itemPerPage) {
        return dialogService.getDialogMessages(dialogId, query, offset, itemPerPage);
    }

    /**
     * Внутренний класс UserIdsRequest.
     * Тело запроса.
     */
    @Data
    @NoArgsConstructor
    public static class UserIdsRequest {
        @JsonProperty("user_ids")
        List<Long> userIds;
    }
}


