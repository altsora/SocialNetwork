package sn.service;

import sn.api.response.CommentResponse;
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
}
