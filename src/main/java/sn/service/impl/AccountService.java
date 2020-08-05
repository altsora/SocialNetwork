package sn.service.impl;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationDTO;
import sn.service.IAccountService;
import sn.service.IPersonService;
import sn.service.MailSenderService;
import sn.service.exceptions.PersonNotFoundException;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Component("account-service")
public class AccountService implements IAccountService {

    @Autowired
    @Qualifier("person-service")
    private IPersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSenderService mailSenderService;

    @Override
    public boolean register(UserRegistrationDTO userRegistrationDTO) {
        if (!checkCaptcha(userRegistrationDTO.getCode())) {
            log.warn("Wrong captcha");
            return false;
        }
        if (!userRegistrationDTO.getPasswd1().equals(userRegistrationDTO.getPasswd2())) {
            low.warn("Passwords do not match");
            return false;
        }

        try {
            if (personService.findByEmail(userRegistrationDTO.getEmail()) != null) {
                log.warn("User with email {} is exist.", userRegistrationDTO.getEmail());
                return false;
            }
            Person person = new Person();
            person.setFirstName(userRegistrationDTO.getFirstName());
            person.setLastName(userRegistrationDTO.getLastName());
            person.setPassword(userRegistrationDTO.getPasswd1());
            person.setEmail(userRegistrationDTO.getEmail());
            if (personService.save(person).isPresent()) {
                log.info("Person successfully registered");
                return true;
            }
            log.error("Error in register method. Person do not registered");
            return false;

        } catch (PersonNotFoundException exception) {
            log.error("Error in register method. User with this email is exist.");
            return false;
        }
    }

    @Override
    public boolean recoveryPassword(String email) {
        try {
            Person person = personService.findByEmail(email);
            if (person == null) {
                log.warn("User with email {} do not exist.", email);
                return false;
            }
            UUID uuid = UUID.randomUUID();
            String randomUUIDString = uuid.toString();
            person.setRecoveryCode(randomUUIDString);
            if (personService.save(person).isPresent()) {
                log.info("Recovery code has been saved");
                CompletableFuture.runAsync(() -> {
                    mailSenderService.send(person.getEmail(), "Password recovery",
                        "For recovery password go to link {server_name}"+randomUUIDString);

                });
                return true;
            }
            log.error("Error in recoveryPassword method. Recovery code do not set and/or email do not sent.");
            return false;
        } catch (PersonNotFoundException exception) {
            log.error("Error in recoveryPassword method. User with this email do not exist.");
            return false;
        }
    }

    @Override
    @Transactional
    public boolean setNewPassword(String recoveryCode, String password) {
        try {
            Person person = personService.findByRecoveryCode(recoveryCode);
            person.setPassword(password);
            if (personService.save(person).isPresent()) {
                log.info("Person password successfully recovered.");
                CompletableFuture.runAsync(() -> {
                    mailSenderService.send(person.getEmail(), "Password recovery",
                            new StringBuffer("Your password has been changed successfully from ")
                                    .append(getIpAddress()).append(" at ").append(LocalDateTime.now()).toString());
                });
                return true;
            }
            log.error("Error in setNewPassword method. Person with recovered password was not saved.");
            return false;
        } catch (PersonNotFoundException exception) {
            log.error("Error in setNewPassword method. Person not found by recovery code.");
            return false;
        }
    }

    private InetAddress getIpAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean changeEmail(String newEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (personService.findByEmail(newEmail) != null) {
                log.warn("User with email {} is exist.", newEmail);
                return false;
            }
            Person person = personService.findByUsername(authentication.getName());
            person.setEmail(newEmail);
            if (personService.save(person).isPresent()) {
                log.info("Person email successfully changed.");
                return true;
            }
            log.error("Error in changeEmail method. Person with changed email was not saved.");
            return false;
        } catch (PersonNotFoundException exception) {
            log.error("Error in changeEmail method. User with this email is exist.");
            return false;
        }
    }

    public boolean checkCaptcha(String code) {
        return true;
    }
}
