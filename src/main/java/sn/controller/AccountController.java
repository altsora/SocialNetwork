package sn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/account")
public class AccountController {

    //Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<Object> register(/*@RequestBody User user*/) {
        //todo
        return null;
    }

    //Восстановить пароль по email.
    @PutMapping("/password/recovery")
    public ResponseEntity<Object> recoveryPassword(/*@RequestBody User user*/) {
        //todo
        return null;
    }

    //Изменить пароль
    @PutMapping("/password/set")
    public ResponseEntity<Object> setPassword(/*@RequestBody FormSetPassword form*/) {
        //todo
        return null;
    }

    //Смена email'а пользователя
    @PutMapping("/email")
    public ResponseEntity<Object> setEmail(/*@RequestBody User user*/) {
        //todo
        return null;
    }

    //Редактирование настроек оповещения
    @PutMapping("/notifications")
    public ResponseEntity<Object> putNotifications(/*@RequestBody Notifications notifications*/) {
        //todo
        return null;
    }
}
