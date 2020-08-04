package sn.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.service.IAccountService;
import sn.service.IPersonService;
import sn.service.exceptions.PersonNotFoundException;

import javax.transaction.Transactional;

@Slf4j
@Service
@Component("account-service")
public class AccountService implements IAccountService {

    @Autowired
    @Qualifier("person-service")
    private IPersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public boolean setNewPassword(String recoveryCode, String password) {
        try {
            Person person = personService.findByRecoveryCode(recoveryCode);
            person.setPassword(password);
            if (personService.save(person).isPresent()) {
                log.info("Person password successfully recovered.");
                return true;
            }
            log.error("Error in setNewPassword method. Person with recovered password was not saved.");
            return false;
        } catch (PersonNotFoundException exception) {
            log.error("Error in setNewPassword method. Person not found by recovery code.");
            return false;
        }
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
}
