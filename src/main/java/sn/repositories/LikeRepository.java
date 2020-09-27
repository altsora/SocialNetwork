package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.model.Like;
import sn.model.enums.LikeType;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(value = "SELECT person_id FROM likes WHERE item_id = :itemId AND liketype = :likeType", nativeQuery = true)
    List<Long> getUsersOfLike(
            @Param("itemId") long itemId,
            @Param("likeType") LikeType likeType
    );
}
