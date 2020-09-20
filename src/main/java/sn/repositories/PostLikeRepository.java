package sn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.model.PostLike;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT pl FROM PostLike pl WHERE pl.person.id = :personId AND pl.post.id = :postId")
    PostLike findByPersonIdAndPostId(
            @Param("personId") long personId,
            @Param("postId") long postId
    );

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    int getCount(@Param("postId") long postId);

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId")
    List<PostLike> findAllByPostId(@Param("postId") long postId);
}
