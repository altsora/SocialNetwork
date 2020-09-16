package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sn.api.response.*;
import sn.model.Dialog;
import sn.service.impl.DialogService;
import sn.service.impl.MessageService;
import sn.utils.ErrorUtil;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    public void getUserDialogList() {

    }

    @Test
    public void createDialogIsOk() throws Exception{
        // Момент первый: возникает ошибка из-за одноимённых полей (DialogResponse#dialogDataSet и DialogResponse#dialogMessages).
        // Пока что закомментил @JsonValue над dialogMessages

        // Момент второй: не проходит DialogResponse через asJsonString (скрины кидал). А должен
        DialogController.UserIdsRequest request = new DialogController.UserIdsRequest();
        DialogResponse dialogResponse1 = DialogResponse.builder().build();
        System.out.println("DialogResponse (asJsonString): " + asJsonString(dialogResponse1));  // Получается null
        System.out.println("DialogResponse (constructor) : " + dialogResponse1);
        System.out.println();
        ErrorResponse errorResponse = ErrorResponse.builder().build();
        System.out.println("ErrorResponse (asJsonString): " + asJsonString(errorResponse));
        System.out.println("ErrorResponse (constructor): " + errorResponse);

        //==============================================================================================================

        Dialog dialog = new Dialog();
        dialog.setId(5);
        ServiceResponse<DialogResponse> serviceResponse =
                new ServiceResponse<>(DialogResponse.builder().dialogId(dialog.getId()).build());
        ResponseEntity<ServiceResponse<DialogResponse>> responseEntity = ResponseEntity.status(HttpStatus.OK).body(serviceResponse);

        Mockito.doReturn(responseEntity)
                .when(dialogService)
                .createDialog(request);

        mockMvc.perform(post("/dialogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(responseEntity.getBody())))
                .andReturn();
    }

    @Test
    public void getUnreadedMessagesCount() {
    }

    @Test
    public void deleteDialogById() {
    }

    @Test
    public void addUsers() {
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

        ServiceResponse<MessageIdResponse> serviceResponse = new ServiceResponse<>(MessageIdResponse.builder().build());

        Mockito.doReturn(ResponseEntity.ok(serviceResponse))
                .when(dialogService)
                .removeMessage(dialogId, messageId);

        mockMvc.perform(delete("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(serviceResponse)))
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

        ResponseEntity<ServiceResponse<AbstractResponse>> responseEntity = ErrorUtil.badRequest("bad");

        Mockito.doReturn(responseEntity)
                .when(dialogService)
                .removeMessage(dialogId, messageId);

        mockMvc.perform(delete("/dialogs/{dialog_id}/messages/{message_id}", dialogId, messageId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(responseEntity.getBody())))
                .andReturn();
    }

    @Test
    public void editMessage() {
    }

    @Test
    public void recoverMessage() {
    }
}