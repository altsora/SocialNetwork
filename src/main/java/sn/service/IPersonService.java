package sn.service;

import sn.api.requests.PersonEditRequest;
import sn.api.response.PersonResponse;
import sn.model.Person;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс IPersonService.
 * Методы для работы Person.
 */
public interface IPersonService {

    Person findByEmail(String email) throws Exception;
    Person findByUsername(String username) throws Exception;
    Optional<Person> save(Person person);

    Person findById(long personId);

    Person updatePerson(Person person, PersonEditRequest personEditRequest);

    void deleteById(long personId);

    boolean changeUserLockStatus(long personId);

    PersonResponse getPersonResponse(Person person);

    int getTotalCountUsers();

    List<Person> searchPersons(String firstName, String lastName, Integer ageFrom,
                               Integer ageTo, Integer offset, Integer itemPerPage
    );
}
