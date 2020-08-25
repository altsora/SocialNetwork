package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.model.FriendshipStatus;

@Repository
public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatus, Long> {

}
