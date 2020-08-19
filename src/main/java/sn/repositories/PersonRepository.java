package sn.repositories;

import sn.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface PersonRepository.
 * Data layer for Person entity.
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByFirstName(String firstName);
    Optional<Person> findByLastName(String lastName);
    Optional<Person> findByEmail(String email);
    Optional<Person> findByPhone(String phone);
    void deleteByEmail(String email);
}
