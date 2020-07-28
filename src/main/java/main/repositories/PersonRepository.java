package main.repositories;

import main.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persons
 */

@Repository
public interface PersonRepository extends CrudRepository<Integer, Person> {

}
