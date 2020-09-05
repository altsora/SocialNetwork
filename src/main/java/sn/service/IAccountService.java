package sn.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sn.api.requests.PersonEditRequest;
import sn.api.response.ErrorResponse;
import sn.api.response.PersonResponse;
import sn.model.Person;
import sn.model.dto.account.UserRegistrationRequest;

import java.util.List;

/**
 * Интерфейс IAccountService.
 * Методы для работы с Account.
 */
public interface IAccountService {

    boolean exists(long personId);

    boolean changeUserLockStatus(long personId);

    Person updatePerson(Person person, PersonEditRequest personEditRequest);

    PersonResponse getPersonResponse(Person person);

    List<Person> searchPersons(String firstName, String lastName, Integer ageFrom,
                               Integer ageTo, Integer offset, Integer itemPerPage);

    boolean setNewPassword(String password);

    boolean changeEmail(String newEmail);

    boolean register(UserRegistrationRequest userRegistrationRequest);

    boolean recoveryPassword(String email);

    Person findCurrentUser();

    ErrorResponse notFoundByIdResponse(long personId);

    ErrorResponse unauthorizedResponse();
}
