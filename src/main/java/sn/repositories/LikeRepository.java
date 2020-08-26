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
    @Query("SELECT l FROM Like l WHERE l.person.id = :personId AND l.likeType = :likeType AND l.itemId = :itemId")
    Like findLike(
            @Param("personId") long personId,
            @Param("type") LikeType likeType,
            @Param("itemId") long itemId
    );

    @Query("SELECT l FROM Like l WHERE l.likeType = :likeType AND l.itemId = :itemId")
    List<Like> findAllByTypeAndItemId(
            @Param("type") LikeType likeType,
            @Param("itemId") long itemId
    );

    @Query("SELECT COUNT(l) FROM Like l WHERE l.likeType = :likeType AND l.itemId = :itemId")
    int getCountByTypeAndItemId(
            @Param("type") LikeType likeType,
            @Param("itemId") long itemId
    );
}
