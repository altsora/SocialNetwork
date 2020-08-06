package sn.service;

import sn.model.Person;
import sn.service.exceptions.PersonNotFoundException;

import java.util.Optional;

/**
 * Интерфейс IPersonService.
 * Методы для работы Person.
 */
public interface IPersonService {

    Person findByRecoveryCode(String recoveryCode) throws PersonNotFoundException;
    Person findByEmail(String email) throws PersonNotFoundException;
    Person findByUsername(String username) throws PersonNotFoundException;
    Optional<Person> save(Person person);
}
