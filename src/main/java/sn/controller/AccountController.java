package sn.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.ResponseDataMessage;
import sn.api.response.ServiceResponse;
import sn.model.dto.account.UserRegistrationRequest;
import sn.service.AccountService;

/**
 * Класс AccountController.
 * REST-контроллер для работы с аккаунтом.
 *
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Метод register.
     * Регистрация пользователя.
     * POST запрос /api/v1/register
     *
     * @param userRegistrationRequest тело запроса в формате Json.
     * @return 200 - регистрация прошла успешно, 400 - ошибка во время регистрации.
     * @see UserRegistrationRequest ;
     */
    @PostMapping("/register")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> register(
            @RequestBody UserRegistrationRequest userRegistrationRequest) {
        return accountService.register(userRegistrationRequest) ? ResponseEntity.status(HttpStatus.OK).body(
                new ServiceResponse<>(new ResponseDataMessage("Registration successful"))
        ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    /**
     * Метод recoveryPassword.
     * Восстановление пароля пользователя.
     * PUT запрос /api/v1/password/recovery
     *
     * @param email email, на который отправляется письмо с ссылкой для восстановления.
     * @return 200 - новый пароль отправлен на почту пользователя, 400 - произошла ошибка.
     */
    @PutMapping("/password/recovery")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> recoveryPassword(@RequestBody String email) {
        if (!Strings.isNotEmpty(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Email is null or empty")));
        }
        return accountService.recoveryPassword(email) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResponse<>(new ResponseDataMessage("Recovery information was sent to e-mail"))
                ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    /**
     * Метод setPassword.
     * Изменение пароля пользователя.
     * PUT запрос /api/v1/password/set
     *
     * @param password новый пароль пользователя.
     * @return 200 - пароль изменен, 400 - произошла ошибка.
     */
    @PutMapping("/password/set")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> setPassword(@RequestBody String password) {
        if (!Strings.isNotEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("New password null or empty")));
        }
        return accountService.setNewPassword(password) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResponse<>(new ResponseDataMessage("Person password successfully changed"))
                ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    /**
     * Метод setEmail.
     * Изменение почтового адреса пользователя.
     * PUT запрос /api/v1/email
     *
     * @param email новый email пользователя.
     * @return 200 - почтовый адрес изменен, 400 - произошла ошибка.
     */
    @PutMapping("/email")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> setEmail(@RequestBody String email) {
        if (!Strings.isNotEmpty(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Email is null or empty")));
        }
        return accountService.changeEmail(email) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResponse<>(new ResponseDataMessage("Person email successfully changed"))
                ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    //Редактирование настроек оповещения
    @PutMapping("/notifications")
    public ResponseEntity<Object> putNotifications(/*@RequestBody Notifications notifications*/) {
        //todo
        return null;
    }
}
