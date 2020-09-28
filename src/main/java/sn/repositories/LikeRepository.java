package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.model.Like;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query(value = "SELECT person_id FROM likes WHERE item_id = :itemId AND like_type = :likeType", nativeQuery = true)
    List<Long> getUsersOfLike(
            @Param("itemId") long itemId,
            @Param("likeType") String likeType
    );

    @Query(value = "SELECT count(id) FROM likes WHERE person_id = :id AND item_id = :itemId AND like_type = :likeType", nativeQuery = true)
    Integer likeExist(@Param("id") long id,
                      @Param("itemId") long itemId,
                      @Param("likeType") String type);
    @Query(value = "SELECT id FROM likes WHERE person_id = :id AND item_id = :itemId AND like_type = :likeType", nativeQuery = true)
    Long findLikeId(@Param("id") long id,
                    @Param("itemId") long itemId,
                    @Param("likeType") String type);
}
