package sn.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sn.model.Person;
import sn.repositories.PersonRepository;
import sn.service.IPersonService;
import sn.service.exceptions.PersonNotFoundException;

import java.util.Optional;

@Service
@Component("person-service")
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public Person findByRecoveryCode(String recoveryCode) throws PersonNotFoundException {
        return personRepository.findByRecoveryCode(recoveryCode)
                .orElseThrow(() -> new PersonNotFoundException("Person not found by recovery code."));
    }

    @Override
    public Person findByEmail(String email) throws PersonNotFoundException {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException("Person not found by email."));
    }

    @Override
    public Person findByUsername(String username) throws PersonNotFoundException {
        return personRepository.findByEmail(username)
                .orElseThrow(() -> new PersonNotFoundException("Person not found by username."));
    }

    @Override
    public Optional<Person> save(Person person) {
        return Optional.of(personRepository.save(person));
    }
}
