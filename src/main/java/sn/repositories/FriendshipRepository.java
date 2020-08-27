package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query(value = "SELECT COUNT(*) FROM friendship"
        + " LEFT JOIN friendship_status on friendship.status_id = friendship_status.id"
        + " WHERE src_person_id = ?1 OR dst_person_id = ?1"
        + " AND code = 'FRIEND'",
        nativeQuery = true)
    int findFriendsByPersonIdCount(long id);
}
