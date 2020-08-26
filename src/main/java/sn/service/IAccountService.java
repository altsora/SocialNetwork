package sn.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sn.api.requests.PersonEditRequest;
import sn.api.response.PersonResponse;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;

import java.util.List;

/**
 * Интерфейс IAccountService.
 * Методы для работы с Account.
 */
public interface IAccountService {

    Person findByEmail(String email) throws UsernameNotFoundException;

    Person findById(long personId);

    boolean changeUserLockStatus(long personId);

    Person updatePerson(Person person, PersonEditRequest personEditRequest);

    PersonResponse getPersonResponse(Person person);

    int getTotalCountUsers();

    List<Person> searchPersons(String firstName, String lastName, Integer ageFrom,
                               Integer ageTo, Integer offset, Integer itemPerPage);

    void deleteById(long personId);

    boolean setNewPassword(String password);

    boolean changeEmail(String newEmail);

    boolean register(UserRegistrationRequest userRegistrationRequest);

    boolean recoveryPassword(String email);

    Person findCurrentUser();

}
