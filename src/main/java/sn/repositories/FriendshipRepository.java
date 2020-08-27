package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query(value = "SELECT COUNT(*) FROM friendship"
        + " WHERE src_person_id = ?1 OR dst_person_id = ?1"
        + " AND status = 'FRIEND'",
        nativeQuery = true)
    int findFriendsByPersonIdCount(long id);
}
