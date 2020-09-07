package sn.controller;

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


