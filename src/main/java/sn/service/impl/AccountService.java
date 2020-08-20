package sn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;
import sn.service.IAccountService;
import sn.service.IPersonService;
import sn.service.MailSenderService;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Класс AccountService.
 * Сервисный слой для аккаунта пользователя.
 *
 * @version 1.0
 */
@Slf4j
@Service("account-service")
public class AccountService implements IAccountService {

    @Autowired
    @Qualifier("person-service")
    private IPersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private Authentication authentication;

    /**
     * Метод register.
     * Регистрация нового пользователя.
     *
     * @param userRegistrationRequest - данные с веб-формы.
     * @return true - если пользователь сохранен в базу, false - не совпали пароли, есть пользователь с такой же почтой,
     * база данных недоступна.
     * @see UserRegistrationRequest ;
     */
    @Override
    public boolean register(UserRegistrationRequest userRegistrationRequest) {
        //todo если заработает на фронте
//        if (!checkCaptcha(userRegistrationRequest.getCode())) {
//            log.warn("Wrong captcha");
//            return false;
//        }
        if (!userRegistrationRequest.getPasswd1().equals(userRegistrationRequest.getPasswd2())) {
            log.warn("Passwords do not match");
            return false;
        }

        try {
            personService.findByEmail(userRegistrationRequest.getEmail());
        } catch (UsernameNotFoundException exception) {
            Person person = new Person();
            person.setFirstName(userRegistrationRequest.getFirstName());
            person.setLastName(userRegistrationRequest.getLastName());
            person.setPassword(passwordEncoder.encode(userRegistrationRequest.getPasswd1()));
            person.setEmail(userRegistrationRequest.getEmail());
            if (personService.save(person).isPresent()) {
                log.info("Person successfully registered");
                return true;
            }
            log.error("Error in register method. Person do not registered");
            return false;
        }
        log.error("User [{}] is exists.", userRegistrationRequest.getEmail());
        return false;
    }

    /**
     * Метод recoveryPassword.
     * Метод для восстановления пароля.
     *
     * @param email почта пользователя.
     * @return true - новый пароль сохранен и отправлен пользователю, false - person не найден в базе, база недоступна.
     */
    @Override
    public boolean recoveryPassword(String email) {
        try {
            Person person = personService.findByEmail(email);
            String newPassword = generateNewPassword(9);
            person.setPassword(passwordEncoder.encode(newPassword));

            if (personService.save(person).isPresent()) {
                log.info("New password set to the person");
                CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                        "Your new password: " + newPassword));
                return true;
            }
            log.error("Error in recoveryPassword method. Recovery code do not set and/or email do not sent.");
            return false;
        } catch (UsernameNotFoundException exception) {
            log.error("Error in recoveryPassword method. User with this email do not exist.");
            return false;
        }
    }

    /**
     * Метод setNewPassword.
     * Осознанная смена пароля.
     *
     * @param password новый пароль.
     * @return true - если пароль изменен, false - если пользователь не найден в базе, база недоступна.
     */
    @Override
    @Transactional
    public boolean setNewPassword(String password) {
        try {
            Person person = personService.findByEmail(authentication.getName());
            person.setPassword(passwordEncoder.encode(password));
            if (personService.save(person).isPresent()) {
                log.info("Person password successfully recovered.");
                CompletableFuture.runAsync(() -> mailSenderService.send(person.getEmail(), "Password recovery",
                        new StringBuffer("Your password has been changed successfully from ")
                                .append(getIpAddress()).append(" at ").append(LocalDateTime.now()).toString()));
                return true;
            }
        } catch (UsernameNotFoundException exception) {
            log.error(exception.getMessage());
            log.error("Password change cancelled. Person not found in database by email: [{}]", authentication.getName());
        }
        return false;
    }

    /**
     * Метод getIpAddress.
     * Получение ip-адреса пользователя.
     *
     * @return ip-алрес пользователя.
     */
    private InetAddress getIpAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Can't get ip address.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод changeEmail.
     * Осознанная смена почтового адреса.
     *
     * @param newEmail новый адрес почты.
     * @return true - если адрес измене, false - если существует пользователь с таким же почтовым адресом,
     * пользователь не найден, база недоступна.
     */
    @Override
    @Transactional
    public boolean changeEmail(String newEmail) {
        try {
            Person person = personService.findByEmail(authentication.getName());
            person.setEmail(newEmail);
            if (personService.save(person).isPresent()) {
                log.info("Person email successfully changed.");
                return true;
            }
        } catch (UsernameNotFoundException exception) {
            log.error(exception.getMessage());
            log.error("Email change cancelled. Person not found in database by email: [{}]", authentication.getName());
        }
        return false;
    }

    /**
     * Метод generateNewPassword.
     * Генератор пароля.
     *
     * @param length длина пароля.
     * @return пароль.
     */
    private String generateNewPassword(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
