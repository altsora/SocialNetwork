package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /**
     * Метод getFriendsCount. Число друзей пользователя
     *
     * @param id - пользователь
     */

    @Query(value = "SELECT COUNT(*) FROM friendship"
        + " WHERE src_person_id = ?1 OR dst_person_id = ?1"
        + " AND status = 'FRIEND'",
        nativeQuery = true)
    int getFriendsCount(long id);

    /**
     * Метод getRequestsCount. Число заявок в друзья
     *
     * @param id - пользователь
     */

    @Query(value = "SELECT COUNT(*) FROM friendship"
        + " WHERE dst_person_id = ?1"
        + " AND status = 'REQUEST'",
        nativeQuery = true)
    int getRequestsCount(long id);

    /**
     * Метод getFriendship. Получить объект Friendship
     *
     * @param id       - пользователь
     * @param friendId - характерный id второго участника
     * @param status   - тип взаимоотношений
     * @see sn.model.enums.FriendshipStatusCode
     */

    @Query(value = "SELECT * FROM friendship WHERE "
        + "(src_person_id = :id AND dst_person_id =:friendId) "
        + "OR (src_person_id = :friendId AND dst_person_id =:id) "
        + "AND status = :status",
        nativeQuery = true)
    Friendship getFriendship(long id, long friendId, String status);

}
