package sn.service;

import sn.api.response.CommentResponse;
import sn.api.response.PersonResponse;
import sn.api.response.WallPostResponse;
import sn.model.Person;
import sn.model.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс IPostService.
 * Методы для работы с классом Post.
 *
 * @see sn.model.Post
 */

public interface IPostService {
    Post addPost(Person author, String title, String text, LocalDateTime postTime);

    Post findById(long postId);

    List<Post> findAllByPersonId(long personId, int offset, int itemPerPage);

    int getTotalCountPostsByPersonId(long personId);

    WallPostResponse createNewWallPost(Post post, PersonResponse author);

    WallPostResponse getExistsWallPost(Post post, PersonResponse author, List<CommentResponse> comments);

    void putLike(long postId);

    void removeLike(long postId);
}
