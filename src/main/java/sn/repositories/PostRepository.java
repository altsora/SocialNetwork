package sn.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.model.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface PostRepository.
 * Data layer for Post entity.
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see sn.model.Post
 */

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    String POST_TIME = "time";

    @Query("SELECT p FROM Post p WHERE p.author.id = :personId")
    List<Post> findAllByPersonId(@Param("personId") long personId, Pageable pageable);

    @Query("SELECT count(p) FROM Post p WHERE p.author.id = :personId")
    int getTotalCountPostsByPersonId(@Param("personId") long personId);

    @Query(value = "SELECT * FROM posts where posts.time < " +
            ":dateTo and posts.time > :dateFrom and posts.text like " +
            "concat('%',:query,'%')",
            nativeQuery = true)
    List<Post> findAllByTextAndTime
            (@Param("query") String text,
             @Param("dateFrom") LocalDateTime dateFrom,
             @Param("dateTo") LocalDateTime dateTo,
             Pageable pageable);
}
