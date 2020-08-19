package sn.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sn.model.Person;

import java.util.Optional;

/**
 * Интерфейс IPersonService.
 * Методы для работы Person.
 */
public interface IPersonService {

    Person findByEmail(String email) throws UsernameNotFoundException;
    Person findByUsername(String username) throws UsernameNotFoundException;
    Optional<Person> save(Person person);
}
