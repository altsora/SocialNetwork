package sn.service;

import sn.api.requests.PostCommentCreateRequest;
import sn.api.response.CommentResponse;
import sn.api.response.IdResponse;
import sn.model.Comment;

import java.util.List;

/**
 * Интерфейс ICommentService.
 * Методы для работы с классом Comment.
 *
 * @see sn.model.Comment
 */

public interface ICommentService {
    Comment findById(long commentId);

    List<CommentResponse> getCommentsByPostId(long postId);

    CommentResponse createPostComment(long id, PostCommentCreateRequest postCommentCreateRequest);

    CommentResponse editComment(long id, long commentId, PostCommentCreateRequest postCommentCreateRequest);

    IdResponse deleteComment(long id, long commentId);

    CommentResponse recoverComment(long id, long commentId);
}
