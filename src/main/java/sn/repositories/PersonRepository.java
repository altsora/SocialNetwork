package sn.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import sn.model.Person;

import java.util.List;
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

    @Query("SELECT COUNT(p) FROM Person p")
    int getTotalCountUsers();

    //TODO: Работает для MYSQL. Проверить для Postgresql.
    // Отсутствует параметры для городов и стран
    @Query(value = "SELECT p.* FROM persons p WHERE " +
            "CASE WHEN :firstName IS NOT NULL THEN p.first_name = :firstName ELSE TRUE END AND " +
            "CASE WHEN :lastName IS NOT NULL THEN p.last_name = :lastName ELSE TRUE END AND " +
            "CASE WHEN :ageFrom IS NOT NULL THEN (YEAR(NOW()) - YEAR(p.birth_date) >= :ageFrom) ELSE TRUE END AND " +
            "CASE WHEN :ageTo IS NOT NULL THEN (YEAR(NOW()) - YEAR(p.birth_date) <= :ageTo) ELSE TRUE END"
            , nativeQuery = true)
    List<Person> searchPersons(@Param("firstName") String firstName,
                               @Param("lastName") String lastName,
                               @Param("ageFrom") Integer ageFrom,
                               @Param("ageTo") Integer ageTo,
                               Pageable pageable
    );

    @Query(value = "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON person.id = friendship.src_person_id "
        + "WHERE dst_person_id = :id "
        + "AND status = 'FRIEND' "
        + "AND (first_name LIKE %:name% OR last_name LIKE %:name%) "
        + "UNION "
        + "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON person.id = friendship.dst_person_id "
        + "WHERE src_person_id = :id "
        + "AND status = 'FRIEND' "
        + "AND (first_name LIKE %:name% OR last_name LIKE %:name%) "
        + "LIMIT :itemPerPage OFFSET :offset"
        , nativeQuery = true)
    List<Person> findFriends(
        @Param("id") long id,
        @Param("offset") int offset,
        @Param("itemPerPage") int itemPerPage,
        @Param("name") String name);

    @Query(value = "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON person.id = friendship.src_person_id "
        + "WHERE dst_person_id = :id "
        + "AND status = 'REQUEST' "
        + "AND (first_name LIKE %:name% OR last_name LIKE %:name%) "
        + "LIMIT :itemPerPage OFFSET :offset"
        , nativeQuery = true)

    List<Person> findRequests(
        @Param("id") long id,
        @Param("offset") int offset,
        @Param("itemPerPage") int itemPerPage,
        @Param("name") String name);

    @Query(value = "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON (person.id != friendship.src_person_id AND person.id != friendship.dst_person_id)"
        + "WHERE person.city = :city"
        + "LIMIT :itemPerPage OFFSET :offset"
        , nativeQuery = true)

    List<Person> findRecommendedFriends(long id, String city, Integer offset, int itemPerPage);
}
