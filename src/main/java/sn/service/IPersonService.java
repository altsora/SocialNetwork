package sn.service;

import sn.model.Person;

import java.util.Optional;

/**
 * Интерфейс IPersonService.
 * Методы для работы Person.
 */
public interface IPersonService {

    Person findByEmail(String email) throws Exception;
    Person findByUsername(String username) throws Exception;
    Optional<Person> save(Person person);
}
