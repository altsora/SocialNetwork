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
import sn.api.response.AbstractResponse;
import sn.api.response.MessageIdResponse;
import sn.api.response.ServiceResponse;
import sn.service.impl.DialogService;
import sn.service.impl.MessageService;
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
    private MessageService messageService;
    @MockBean
    private DialogService dialogService;

    private static ResponseEntity<ServiceResponse<AbstractResponse>> badRequestResponse = ErrorUtil.badRequest("bad");
    private static ResponseEntity<ServiceResponse> okResponse = ResponseEntity.ok(new ServiceResponse<>());

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
        long dialogId = 1;

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
        long dialogId = 1;

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
    public void addUsersIsOk() throws Exception{
        long dialogId = 1;


        mockMvc.perform(delete("/dialogs/{id}/users", dialogId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    /**
     * Удаление свзяи текущего пользователя и диалога с указанным id. Bad request.
     */
    @Test
    public void addUsersIsBadRequest() {
    }

    @Test
    public void deleteUsers() {
    }

    @Test
    public void invite() {
    }

    @Test
    public void joinToDialog() {
    }

    @Test
    public void getMessages() {
    }

    @Test
    public void readMessage() {
    }

    @Test
    public void getLastActivity() {
    }

    @Test
    public void changeTypingStatus() {
    }

    @Test
    public void sendMessage() {
    }

    /**
     * Удаление пользователя. OK.
     *
     * @throws Exception
     */
    @Test
    public void removeMessageIsOk() throws Exception {
        long dialogId = 1;
        long messageId = 2;

        Mockito.doReturn(ResponseEntity.ok(new ServiceResponse<>()))
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
        long dialogId = 1;
        long messageId = 2;

        Mockito.doReturn(ErrorUtil.badRequest("bad"))
                .when(dialogService)
                .removeMessage(dialogId, messageId);

        mockMvc.perform(delete("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    public void editMessage() {
    }

    @Test
    public void recoverMessage() {
    }
}