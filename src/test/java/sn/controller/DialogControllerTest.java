package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sn.api.requests.MessageSendRequest;
import sn.api.response.AbstractResponse;
import sn.api.response.ServiceResponse;
import sn.service.DialogService;
import sn.utils.ErrorUtil;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс DialogControllerTest.
 * MVC тесты для DialogController.
 *
 * @version 1.0
 * @see DialogController
 */
public class DialogControllerTest extends AbstractWebController {

    @Autowired
    private DialogController dialogController;

    @MockBean
    private DialogService dialogService;

    private static ResponseEntity<ServiceResponse<AbstractResponse>> badRequestResponse = ErrorUtil.badRequest("bad");
    private static ResponseEntity<ServiceResponse<AbstractResponse>> okResponse = ResponseEntity.ok(new ServiceResponse<>());
    private static long dialogId = 1;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Тест на загрузку контекста.
     */
    @Test
    public void contextLoads() {
        assertNotNull(dialogController);
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(dialogController).build();
    }

    //==================================================================================================================

    /**
     * Получение списка диалогов авторизованного пользователя. Bad request.
     */
    @Test
    public void getUserDialogListIsBadRequest() throws Exception {
        int offset = 0;
        int itemPerPage = 20;
        String query = "test query";

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .findPersonDialogsWithQuery(query, offset, itemPerPage);

        mockMvc.perform(get("/dialogs")
                .param("query", query)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение списка диалогов авторизованного пользователя. OK.
     */
    @Test
    public void getUserDialogListIsOk() throws Exception {
        int offset = 0;
        int itemPerPage = 20;
        String query = "test query";

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .findPersonDialogsWithQuery(query, offset, itemPerPage);

        mockMvc.perform(get("/dialogs")
                .param("query", query)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Создание диалога. Bad request.
     */
    @Test
    public void createDialogIsBadRequest() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .createDialog(request);

        mockMvc.perform(post("/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Создание диалога. OK.
     */
    @Test
    public void createDialogIsOk() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .createDialog(request);

        mockMvc.perform(post("/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Успешное получение общего кол-ва непрочитанных сообщений. OK.
     */
    @Test
    public void getUnreadedMessagesCountIsOk() throws Exception {
        Mockito.doReturn(okResponse)
                .when(dialogService)
                .getUnreadMessagesCount();

        mockMvc.perform(get("/dialogs/unreaded"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение общего кол-ва непрочитанных сообщений. Bad request.
     */
    @Test
    public void getUnreadedMessagesCountIsBadRequest() throws Exception {
        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .getUnreadMessagesCount();

        mockMvc.perform(get("/dialogs/unreaded"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление свзяи текущего пользователя и диалога с указанным id. OK.
     */
    @Test
    public void deleteDialogByIdIsOk() throws Exception {
        Mockito.doReturn(okResponse)
                .when(dialogService)
                .deleteDialog(dialogId);

        mockMvc.perform(delete("/dialogs/{id}", dialogId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление свзяи текущего пользователя и диалога с указанным id. Bad request.
     */
    @Test
    public void deleteDialogByIdIsBadRequest() throws Exception {
        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .deleteDialog(dialogId);

        mockMvc.perform(delete("/dialogs/{id}", dialogId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавить пользователей в диалог. OK.
     */
    @Test
    public void addUsersIsOk() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();
        Mockito.doReturn(okResponse)
                .when(dialogService)
                .addUsersToDialog(dialogId, request);

        mockMvc.perform(put("/dialogs/{id}/users", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Добавить пользователей в диалог. Bad request.
     */
    @Test
    public void addUsersIsBadRequest() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();
        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .addUsersToDialog(dialogId, request);

        mockMvc.perform(put("/dialogs/{id}/users", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удалить пользователей из диалога. OK.
     */
    @Test
    public void deleteUsersIsOk() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .deleteUsersFromDialog(dialogId, request);

        mockMvc.perform(delete("/dialogs/{id}/users", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удалить пользователей из диалога. Bad request.
     */
    @Test
    public void deleteUsersIsBadRequest() throws Exception {
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .deleteUsersFromDialog(dialogId, request);

        mockMvc.perform(delete("/dialogs/{id}/users", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Создание ссылки-приглашения в диалог. OK.
     */
    @Test
    public void inviteIsOk() throws Exception {
        Mockito.doReturn(okResponse)
                .when(dialogService)
                .createInviteLink(dialogId);

        mockMvc.perform(get("/dialogs/{id}/users/invite", dialogId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Создание ссылки-приглашения в диалог. Bad request.
     */
    @Test
    public void inviteIsBadRequest() throws Exception {
        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .createInviteLink(dialogId);

        mockMvc.perform(get("/dialogs/{id}/users/invite", dialogId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Присоедениться к диалогу по ссылке-приглашению. OK.
     */
    @Test
    public void joinToDialogIsOk() throws Exception {
        String link = "link";

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .joinUserToDialog(dialogId, link);

        mockMvc.perform(put("/dialogs/{id}/users/join", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(link))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Присоедениться к диалогу по ссылке-приглашению. Bad request.
     */
    @Test
    public void joinToDialogIsBadRequest() throws Exception {
        String link = "link";

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .joinUserToDialog(dialogId, link);

        mockMvc.perform(put("/dialogs/{id}/users/join", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(link))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение списка сообщений диалога. OK.
     */
    @Test
    public void getMessagesIsOk() throws Exception {
        String query = "query";
        int offset = 0;
        int itemPerPage = 20;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .getDialogMessages(dialogId, query, offset, itemPerPage);

        mockMvc.perform(get("/dialogs/{id}/messages", dialogId)
                .param("query", query)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получение списка сообщений диалога. Bad request.
     */
    @Test
    public void getMessagesIsBadRequest() throws Exception {
        String query = "query";
        int offset = 0;
        int itemPerPage = 20;

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .getDialogMessages(dialogId, query, offset, itemPerPage);

        mockMvc.perform(get("/dialogs/{id}/messages", dialogId)
                .param("query", query)
                .param("offset", String.valueOf(offset))
                .param("itemPerPage", String.valueOf(itemPerPage)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Пометить сообщение как "Прочитанное". OK.
     */
    @Test
    public void readMessageIsOk() throws Exception {
        long messageId = 2;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .readMessage(dialogId, messageId);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}/read", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Пометить сообщение как "Прочитанное". Bad request.
     */
    @Test
    public void readMessageIsBadRequest() throws Exception {
        long messageId = 2;

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .readMessage(dialogId, messageId);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}/read", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получить последнюю активность и текущий статус для пользователя, с которым ведется диалог. OK.
     */
    @Test
    public void getLastActivityIsOk() throws Exception {
        long personId = 3;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .getLastActivity(dialogId, personId);

        mockMvc.perform(get("/dialogs/{id}/activity/{user_id}", dialogId, personId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Получить последнюю активность и текущий статус для пользователя, с которым ведется диалог. Bad request.
     */
    @Test
    public void getLastActivityIsBadRequest() throws Exception {
        long personId = 3;

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .getLastActivity(dialogId, personId);

        mockMvc.perform(get("/dialogs/{id}/activity/{user_id}", dialogId, personId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Изменить статус набора текста пользователем в диалоге. OK.
     */
    @Test
    public void changeTypingStatusIsOk() throws Exception {
        long personId = 3;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .changeTypingStatus(dialogId, personId);

        mockMvc.perform(post("/dialogs/{id}/activity/{user_id}", dialogId, personId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Изменить статус набора текста пользователем в диалоге. Bad request.
     */
    @Test
    public void changeTypingStatusIsBadRequest() throws Exception {
        long personId = 3;

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .changeTypingStatus(dialogId, personId);

        mockMvc.perform(post("/dialogs/{id}/activity/{user_id}", dialogId, personId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Отправка сообщения. OK.
     */
    @Test
    public void sendMessageIsOk() throws Exception {
        MessageSendRequest sendRequest = new MessageSendRequest();

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .sendMessage(dialogId, sendRequest);

        mockMvc.perform(post("/dialogs/{id}/messages", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sendRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Отправка сообщения. Bad request.
     */
    @Test
    public void sendMessageIsBadRequest() throws Exception {
        MessageSendRequest sendRequest = new MessageSendRequest();

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .sendMessage(dialogId, sendRequest);

        mockMvc.perform(post("/dialogs/{id}/messages", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sendRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Отправка сообщения. Unauthorized.
     */
    @Test
    public void sendMessageIsUnauthorized() throws Exception {
        MessageSendRequest sendRequest = new MessageSendRequest();

        Mockito.doReturn(ErrorUtil.unauthorized())
                .when(dialogService)
                .sendMessage(dialogId, sendRequest);

        mockMvc.perform(post("/dialogs/{id}/messages", dialogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sendRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление пользователя. OK.
     *
     * @throws Exception
     */
    @Test
    public void removeMessageIsOk() throws Exception {
        long messageId = 2;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .removeMessage(dialogId, messageId);

        mockMvc.perform(delete("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление пользователя. Bad request.
     *
     * @throws Exception
     */
    @Test
    public void removeMessageIsBadRequest() throws Exception {
        long messageId = 2;

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .removeMessage(dialogId, messageId);

        mockMvc.perform(delete("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Редактирование сообщения. OK.
     *
     * @throws Exception
     */
    @Test
    public void editMessageIsOk() throws Exception {
        long messageId = 2;
        MessageSendRequest sendRequest = new MessageSendRequest();

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .editMessage(dialogId, messageId, sendRequest);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sendRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Редактирование сообщения. Bad request.
     *
     * @throws Exception
     */
    @Test
    public void editMessageIsBadRequest() throws Exception {
        long messageId = 2;
        MessageSendRequest sendRequest = new MessageSendRequest();

        Mockito.doReturn(badRequestResponse)
                .when(dialogService)
                .editMessage(dialogId, messageId, sendRequest);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sendRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Восстановление удаленного сообщения. OK.
     *
     * @throws Exception
     */
    @Test
    public void recoverMessageIsOk() throws Exception {
        long messageId = 2;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .recoverMessage(dialogId, messageId);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}/recover", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Восстановление удаленного сообщения. Bad request.
     *
     * @throws Exception
     */
    @Test
    public void recoverMessageIsBadRequest() throws Exception {
        long messageId = 2;

        Mockito.doReturn(okResponse)
                .when(dialogService)
                .recoverMessage(dialogId, messageId);

        mockMvc.perform(put("/dialogs/{dialog_id}/messages/{message_id}/recover", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}