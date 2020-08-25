package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

}
