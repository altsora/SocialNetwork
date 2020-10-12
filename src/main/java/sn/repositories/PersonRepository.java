package sn.repositories;

import org.hibernate.jpa.TypedParameterValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import sn.model.Person;

import java.util.List;
import java.util.Optional;

/**
 * Interface PersonRepository. Data layer for Person entity.
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
    @Query(value = "select p.* from Person p where " +
            "lower(p.first_name) like lower(concat('%', nullif(:firstName, '%'), '%')) " +
            "and lower(p.last_name) like lower(concat('%', nullif(:lastName, '%'), '%')) " +
            "and lower(p.city) like lower(concat('%', nullif(:city, '%'),  '%')) " +
            "and lower(p.country) like lower(concat('%', nullif(:country, '%'),  '%'))"
            , nativeQuery = true)
    List<Person> searchPersons(@Param("firstName") String firstName,
                                      @Param("lastName") String lastName,
                                      @Param("city") String city,
                                      @Param("country") String country,
                                      @Param("ageFrom") TypedParameterValue ageFrom,
                                      @Param("ageTo") TypedParameterValue ageTo,
                                      Pageable pageable
    );

    /**
     * Метод findFriends. Нахождение друзей пользователя
     *
     * @param id          - пользователь
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @param name        - характерное имя или часть именни для поиска
     * @return список Person или пустой список, если друзей нет
     */

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

    /**
     * Метод findRequests. Нахождение заявок в друзья
     *
     * @param id          - пользователь
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @param name        - характерное имя или часть имени для поиска
     * @return список Person или пустой список, если заявок нет
     */

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

    /**
     * Метод findRecommendedFriends. Найти рекомендованных друзей
     *
     * @param id          - пользователь
     * @param offset      - Отступ от начала результирующего списка пользователей.
     * @param itemPerPage - Количество пользователей из результирующего списка, которые представлены для
     *                    отображения.
     * @return список Person или пустой список, если некого рекомендовать
     */

    @Query(value = "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON person.id = friendship.src_person_id "
        + "WHERE person.city = :city "
        + "AND person.id != :id "
        + "AND status != 'FRIEND' "
        + "UNION "
        + "SELECT person.* FROM person "
        + "LEFT JOIN friendship ON person.id = friendship.dst_person_id "
        + "WHERE person.city = :city "
        + "AND person.id != :id "
        + "AND status != 'FRIEND' "
        + "UNION "
        + "SELECT person.* FROM person "
        + "WHERE person.city = :city "
        + "AND person.id != :id "
        + "LIMIT :itemPerPage OFFSET :offset"
        , nativeQuery = true)
    List<Person> findRecommendedFriends(long id, String city, Integer offset, int itemPerPage);
}
