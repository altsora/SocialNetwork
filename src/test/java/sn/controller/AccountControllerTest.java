package sn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sn.model.dto.account.UserRegistrationRequest;
import sn.service.impl.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс AccountControllerTest.
 * MVC тесты для AccountCntroller.
 *
 * @version 1.0
 * @see AccountController
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AccountControllerTest {

    private final static String USER_EMAIL = "bro@malta.com";
    private final static String USER_PASSWORD = "Qwerty0987!";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountController accountController;
    @MockBean
    private AccountService accountService;
    @MockBean
    private PasswordEncoder passwordEncoder;

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
        assertThat(accountController).isNotNull();
    }

    /**
     * Успешная регистрация пользователя.
     *
     * @throws Exception
     */
    @Test
    public void whenRegistrateNewUserThenStatusCodeOKAndRegistrationSuccessful() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        Mockito.doReturn(true).when(accountService).register(userRegistrationRequest);
        this.mockMvc.perform(post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRegistrationRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Registration successful")))
                .andReturn();
    }

    /**
     * Повторная регистрация.
     *
     * @throws Exception
     */
    @Test
    public void whenRegistrateExistingUserThenResponseStatusIsBadRequestAndRegistrationCancelled() throws Exception {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        Mockito.doReturn(false).when(accountService).register(userRegistrationRequest);
        this.mockMvc.perform(post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRegistrationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andExpect(content().string(containsString("Bad request")))
                .andReturn();
    }

    /**
     * Успешное восстановления пароля.
     *
     * @throws Exception
     */
    @Test
    public void whenUserExistAndNewPasswordIsNotEmptyThenResponseStatusIsOKAndPasswordRecover() throws Exception {
        Mockito.doReturn(true).when(accountService).recoveryPassword(USER_EMAIL);
        this.mockMvc.perform(put("/account/password/recovery")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Recovery information was sent to e-mail")))
                .andReturn();
    }

    /**
     * Восстановление пароля для несуществующего пользователя.
     *
     * @throws Exception
     */
    @Test
    public void whenUserNotExistAndNewPasswordIsNotEmptyThenResponseStatusIsBadRequest() throws Exception {
        Mockito.doReturn(false).when(accountService).recoveryPassword(USER_EMAIL);
        this.mockMvc.perform(put("/account/password/recovery")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_EMAIL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andExpect(content().string(containsString("Bad request")))
                .andReturn();
    }

    /**
     * Успешное осознанное узменение пароля.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void whenUserExistAndNewPasswordIsNotEmptyThenPasswordChangeResponseIsOk() throws Exception {
        Mockito.doReturn(true).when(accountService).setNewPassword(USER_PASSWORD);
        this.mockMvc.perform(put("/account/password/set")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_PASSWORD))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Person password successfully changed")))
                .andReturn();
    }

    /**
     * Осознанное изменение пароля несущестующим пользователем.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void whenUserNotExistAndNewPasswordIsNotEmptyThenPasswordChangeResponseIsBadRequest() throws Exception {
        Mockito.doReturn(false).when(accountService).setNewPassword(USER_PASSWORD);
        this.mockMvc.perform(put("/account/password/set")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Service unavailable")))
                .andExpect(content().string(containsString("Bad request")))
                .andReturn();
    }

    /**
     * Успешное осознанное изменение email.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void whenUserExistAndNewEmailIsNotEmptyThenEmailChangeResponseIsOk() throws Exception {
        Mockito.doReturn(true).when(accountService).changeEmail(USER_EMAIL);
        this.mockMvc.perform(put("/account/email")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_EMAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Person email successfully changed")))
                .andReturn();
    }

    /**
     * Осознанное изменение пароля несуществующим пользователем.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser(username = USER_EMAIL)
    public void whenUserNotExistAndNewEmailIsNotEmptyThenEmailChangeResponseIsBadRequest() throws Exception {
        Mockito.doReturn(false).when(accountService).changeEmail(USER_EMAIL);
        this.mockMvc.perform(put("/account/email")
                .contentType(MediaType.TEXT_PLAIN)
                .content(USER_EMAIL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Service unavailable")))
                .andExpect(content().string(containsString("Bad request")))
                .andReturn();
    }

}
