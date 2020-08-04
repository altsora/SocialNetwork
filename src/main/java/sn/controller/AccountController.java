package sn.controller;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.api.ResponseDataMessage;
import sn.api.ServiceResponse;
import sn.model.dto.EmailDTO;
import sn.model.dto.recoveryPassword.RecoveryFormData;
import sn.service.IAccountService;
import sn.service.IPersonService;

@Slf4j
@RestController
@RequestMapping("api/v1/account")
public class AccountController {

    @Autowired
    @Qualifier("person-service")
    private IPersonService personService;

    @Autowired
    @Qualifier("account-service")
    private IAccountService accountService;

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<Object> register(/*@RequestBody User user*/) {
        //todo
        return null;
    }


    @PutMapping("/password/recovery")
    public ResponseEntity<Object> recoveryPassword(/*@RequestBody User user*/) {
        //todo
        return null;
    }

    @PutMapping("/password/set")
    public ResponseEntity<ServiceResponse<ResponseDataMessage>> setPassword(@RequestBody RecoveryFormData recoveryFormData) {
        if (Strings.isNullOrEmpty(recoveryFormData.getToken()) || Strings.isNullOrEmpty(recoveryFormData.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ServiceResponse<>("Bad request", new ResponseDataMessage("Recovery data null or empty")));
        }
        return accountService.setNewPassword(recoveryFormData.getToken(), recoveryFormData.getPassword()) ?
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
