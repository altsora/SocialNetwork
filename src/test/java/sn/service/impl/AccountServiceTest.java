package sn.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;
import sn.service.IAccountService;
import sn.service.MailSenderService;

import java.util.Optional;

/**
 * Класс AccountServiceTest.
 * Модульные тесты для AccountService.
 * @see AccountService;
 */
@SpringBootTest
class AccountServiceTest {

    private final static String USER_EMAIL = "test@test.ru";
    private final static String USER_PASSWD1 = "PassW#or_d";
    private final static String USER_PASSWD2 = "PassW#or_d";
    private final static String NEW_USER_PASSWD = "Q_werty0987#";

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;

    @MockBean
    private PersonService personService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MailSenderService mailSenderService;

    @MockBean
    private Authentication authentication;

    /**
     * Корректный ввод данных поьльзователем.
     */
    @Test
    public void whenUserDataIsCorrectAndAnotherUserNotExistsByEmailThenRegistrationResultIsTrue() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setEmail(USER_EMAIL);
        userRegistrationRequest.setPasswd1(USER_PASSWD1);
        userRegistrationRequest.setPasswd2(USER_PASSWD2);
        Person person = new Person();
        person.setFirstName(userRegistrationRequest.getFirstName());
        person.setLastName(userRegistrationRequest.getLastName());
        person.setPassword(passwordEncoder.encode(userRegistrationRequest.getPasswd1()));
        person.setEmail(userRegistrationRequest.getEmail());
        Mockito.doThrow(new UsernameNotFoundException("Person not found by email."))
                .when(personService).findByEmail(USER_EMAIL);
        Mockito.doReturn(Optional.of(person)).when(personService).save(person);
        boolean isPersonRegistered = accountService.register(userRegistrationRequest);
        Assert.assertTrue(isPersonRegistered);
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(personService, Mockito.times(1)).save(person);
    }

    /**
     * Регистрация при несовпадающих паролях.
     */
    @Test
    public void whenUserPasswordsMismatchThenRegistrationResultIsFalse() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setEmail(USER_EMAIL);
        userRegistrationRequest.setPasswd1(USER_PASSWD1);
        userRegistrationRequest.setPasswd2(NEW_USER_PASSWD);
        boolean isPersonRegistered = accountService.register(userRegistrationRequest);
        Assert.assertFalse(isPersonRegistered);
        Mockito.verify(personService, Mockito.times(0)).findByEmail(USER_EMAIL);
    }

    /**
     * Регистрация, когда пользователь с таким же email существует.
     */
    @Test
    public void whenUserAlreadyExistsThenRegistrationResultIsFalse() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
        userRegistrationRequest.setEmail(USER_EMAIL);
        userRegistrationRequest.setPasswd1(USER_PASSWD1);
        userRegistrationRequest.setPasswd2(USER_PASSWD2);
        Person person = new Person();
        person.setEmail(USER_EMAIL);
        Mockito.doReturn(person).when(personService).findByEmail(USER_EMAIL);
        boolean isPersonRegistered = accountService.register(userRegistrationRequest);
        Assert.assertFalse(isPersonRegistered);
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
    }

    /**
     * Восстановление пароля для существующего пользователя.
     */
    @Test
    void whenUserExistsByEmailThenRecoveryPasswordResultIsTrue() {
        Person person = new Person();
        person.setEmail(USER_EMAIL);
        person.setPassword(USER_PASSWD1);
        Mockito.doReturn(person).when(personService).findByEmail(USER_EMAIL);
        person.setPassword(NEW_USER_PASSWD);
        Mockito.doReturn(Optional.of(person)).when(personService).save(person);
        boolean isRecoveryPasswordEmailSend = accountService.recoveryPassword(USER_EMAIL);
        Assert.assertTrue(isRecoveryPasswordEmailSend);
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(personService, Mockito.times(1)).save(person);
    }

    /**
     * Восстановление пароля, когда по заданному email не найти не одного пользователя.
     */
    @Test
    void whenUserNotFoundByEmailThenRecoveryPasswordResultIsFalse() {
        Person person = new Person();
        person.setEmail(USER_EMAIL);
        person.setPassword(USER_PASSWD1);
        Mockito.doThrow(new UsernameNotFoundException("Person not found by email."))
                .when(personService).findByEmail(USER_EMAIL);
        boolean isRecoveryPasswordEmailSend = accountService.recoveryPassword(USER_EMAIL);
        Assert.assertFalse(isRecoveryPasswordEmailSend);
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(personService, Mockito.times(0)).save(person);
    }

    /**
     * Изменение пароля авторизованным пользователем.
     */
    @Test
    void whenUserIsAuthorizedThenSetPasswordResultIsTrue() {
        Person person = new Person();
        person.setEmail(USER_EMAIL);
        Mockito.doReturn(USER_EMAIL).when(authentication).getName();
        Mockito.doReturn(person).when(personService).findByEmail(USER_EMAIL);
        Mockito.doReturn(Optional.of(person)).when(personService).save(person);
        Assert.assertTrue(accountService.setNewPassword(NEW_USER_PASSWD));
        Mockito.verify(authentication, Mockito.times(1)).getName();
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(personService, Mockito.times(1)).save(person);
    }

    /**
     * Изменение email авторизованного пользователя.
     */
    @Test
    void whenUserIsAuthorizedThenChangeEmailResultIsTrue() {
        Person person = new Person();
        person.setEmail(USER_EMAIL);
        person.setPassword(USER_PASSWD1);
        Mockito.doReturn(USER_EMAIL).when(authentication).getName();
        Mockito.doReturn(person).when(personService).findByEmail(USER_EMAIL);
        Mockito.doReturn(Optional.of(person)).when(personService).save(person);
        Assert.assertTrue(accountService.changeEmail("some@email.ru"));
        Mockito.verify(authentication, Mockito.times(1)).getName();
        Mockito.verify(personService, Mockito.times(1)).findByEmail(USER_EMAIL);
        Mockito.verify(personService, Mockito.times(1)).save(person);
    }


}