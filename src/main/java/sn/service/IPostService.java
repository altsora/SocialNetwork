package sn.service;

import sn.api.requests.PostEditRequest;
import sn.api.response.CommentResponse;
import sn.api.response.PersonResponse;
import sn.api.response.PostResponse;
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

    Person findCurrentUser();

    List<PostResponse> findPosts(String text, long dateFrom, long dateTo,
                           int offset, int itemPerPage);

    PostResponse findPostById(long id);

    PostResponse editPost
            (long id, long publishDate, PostEditRequest postEditRequest);
}
