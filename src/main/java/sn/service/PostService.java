package sn.service;

import sn.model.Person;
import sn.model.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс PostService.
 * Методы для работы с классом Post.
 *
 * @see sn.model.Post
 */

public interface PostService {
    Post findById(long postId) throws PostNotFoundException;

    List<Post> findAllByPersonId(long personId, int offset, int itemPerPage);

    Post addPost(Person author, String title, String text, LocalDateTime postTime);

    int getTotalCountPostsByPersonId(long personId);
}
