package sn.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.model.Comment;

import java.util.List;

/**
 * Interface CommentRepository.
 * Data layer for Comment entity.
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see sn.model.Comment
 */

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    String COMMENT_TIME = "time";

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllCommentsByPostId(@Param("postId") long postId, Sort sort);
}
