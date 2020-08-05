package sn.controller;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.ResponseDataMessage;
import sn.api.response.ServiceResponse;
import sn.model.dto.account.EmailDTO;
import sn.model.dto.account.UserRegistrationDTO;
import sn.model.dto.account.recoveryPassword.PasswordDTO;
import sn.service.IAccountService;
import sn.service.IPersonService;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    @Qualifier("person-service")
    private IPersonService personService;

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> register(
            @RequestBody UserRegistrationDTO userRegistrationDTO)
            throws IllegalAccessException {
        if (!userRegistrationDTO.selfCheck()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Regitstration data empty or null")));
        }
        return accountService.register(userRegistrationDTO) ? ResponseEntity.status(HttpStatus.OK).body(
                new ServiceResponse<>(new ResponseDataMessage("Registration successfull"))
        ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    @PutMapping("/password/recovery")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> recoveryPassword(@RequestBody EmailDTO emailDTO) {
        if (Strings.isNullOrEmpty(emailDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Email is null or empty")));
        }
        return accountService.recoveryPassword(emailDTO.getEmail()) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResponse<>(new ResponseDataMessage("Recovery information was sent to e-mail"))
                ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    @PutMapping("/password/set")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> setPassword(@RequestBody PasswordDTO passwordDTO) {
        if (Strings.isNullOrEmpty(passwordDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("New password null or empty")));
        }
        return accountService.setNewPassword(passwordDTO.getPassword()) ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ServiceResponse<>(new ResponseDataMessage("Person password successfully recovered"))
                ) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ServiceResponse<>("Bad request", new ResponseDataMessage("Service unavailable"))
                );
    }

    @PutMapping("/email")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> setEmail(@RequestBody EmailDTO emailDTO) {
        if (Strings.isNullOrEmpty(emailDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Email is null or empty")));
        }
        return accountService.changeEmail(emailDTO.getEmail()) ?
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
