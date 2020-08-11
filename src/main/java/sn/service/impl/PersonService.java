package sn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.repositories.PersonRepository;
import sn.service.IPersonService;

import java.util.Optional;

/**
 * Класс PersonService.
 * Сервисный слой для Person.
 * Имплементирует IPersonService.
 * @see sn.service.IPersonService
 * @version 1.0
 */
@Service("person-service")
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Метод findByEmail.
     * Поиск по email.
     * @param email - почтовый адрес.
     * @return Person.
     * @throws Exception - если пользователь не найден по email.
     */
    @Override
    public Person findByEmail(String email) throws Exception {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Person not found by email."));
    }

    /**
     * Метод findByUsername.
     * Поиск по имени пользователя.
     * @param username - имя пользователя.
     * @return Person.
     * @throws Exception - если пользователь не найден по имени пользователя.
     */
    @Override
    public Person findByUsername(String username) throws Exception {
        return personRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("Person not found by username."));
    }

    /**
     * Метод save.
     * Сохранение пользователя в базу данных.
     * @param person - объект класса Person.
     * @return - результат сохранения в Optional обертке.
     */
    @Override
    public Optional<Person> save(Person person) {
        return Optional.of(personRepository.save(person));
    }
}
